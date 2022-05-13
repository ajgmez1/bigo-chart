(function() {

    // helper methods
    var Helper = (function() {
        return {
            request: function(url, opts, fn) {
                return fetch(new Request(url, opts)).then((r) => r.json()).then(fn)
                        .catch((e) => console.error(url, response.status, e));
            },
            createSelectHTML: function(dom, values) {
                var option, i;
        
                dom.innerHTML = '';
        
                for (i = 0; i < values.length; i++) {
                    option = document.createElement("option");
                    option.setAttribute('value', values[i]);
                    option.text = values[i];
                    dom.appendChild(option);
                }
            }
        };
    })();

    // Create the chart
    var chart = new Chart(document.querySelector('#chart'), {
        type: 'line',
        data: {
            labels: [],
            datasets: [{
                label: '',
                data: [],
                borderColor: '#296dab'
            }]
        },
        options: {
            scales: {
                yAxes: [{
                    scaleLabel: {
                        display: true,
                        labelString: 'Nanoseconds'
                    }
                }],
                xAxes: [{
                    scaleLabel: {
                        display: true,
                        labelString: 'Input size'
                    }
                }]
            }     
        }
    });

    var Form = (function() {
        var container = document.getElementById('container');
        var closeInfo = document.querySelector('#info-close');
        var master = document.getElementById('master-form');
        var addForm = master.querySelector('#addForm');
        var testAll = master.querySelector('#testAll');
        var inputSize = master.querySelector('#inputSize');
        var dataPoint = master.querySelector('#dataPoint');
        var inputSizeLabel = master.querySelector('#inputSizeLabel');
        var dataPointLabel = master.querySelector('#dataPointLabel');
        var lastCValue = "";

        inputSizeLabel.name = inputSizeLabel.textContent;
        dataPointLabel.name = dataPointLabel.textContent;
        
        var config = {
            next: 0,
            max: 5,
            forms: [],
            defaultColors: ['#1779ba', '#3adb76', '#a53b2a', '#cc8b00', '#767676']
        };

        // master form events
        addForm.addEventListener('click', () => {
            if (!addForm.hasAttribute('disabled')) {
                Form.createForm();
            }
        });
        testAll.addEventListener('click', () => testAllFn());
        inputSize.addEventListener('change', () => {
            setRangeLabel(inputSizeLabel, inputSize);
            testAllFn();
        });
        dataPoint.addEventListener('change', () => {
            setRangeLabel(dataPointLabel, dataPoint);
            testAllFn();
        });
        closeInfo.addEventListener('click', () => {
            $('#info-cell').fadeOut('fast', () => chart.resize());
        });

        // private methods
        const addEvents = function(form) {
            var collections = form.querySelector('[name=collections]');
            var operations = form.querySelector('[name=operations]');
            var submit = form.querySelector('[name=submit]');
            var info = form.querySelector('[name=info]');
            var deleteBtn = form.querySelector("[name=delete]");
            var color = form.querySelector('[name=color]');

            // initialize fields
            const setColors = config.forms.map((f) => f.color);
            color.value = config.defaultColors.filter((c) => !setColors.includes(c))[0];
            form.color = color.value;

            // Events
            collections.onchange = () => {
                lastCValue = collections.value;
                const values = operations.c[collections.value].methods.map((m) => m.type);
                Helper.createSelectHTML(operations, values);
            };
            color.onchange = () => {
                form.color = color.value;
            };
            submit.onclick = (e) => {
                if (!e.bulk && submit.hasAttribute('disabled')) return;
                disableAll(true);

                return Helper.request('api/collection/test', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({
                        collection: collections.value,
                        operation: operations.value,
                        n: inputSize.value,
                        points: dataPoint.value
                    })
                }, (json) => {
                    chart.data.labels = [];
                    chart.data.datasets[form.id] = {};
                    chart.data.datasets[form.id].label = collections.value + ' - ' + operations.value;
                    chart.data.datasets[form.id].borderColor = color.value;
                    chart.data.datasets[form.id].data = [];
                    for (var n in json) {
                        chart.data.labels.push(n);
                        chart.data.datasets[form.id].data.push({ x:n, y:json[n] });
                    }

                    if (!e.bulk) {
                        chart.update();
                        chart.options.animation.onComplete = () => {
                            disableAll(false);
                        }
                    }
                });
            };
            info.addEventListener('click', () => {
                const collection = operations.c[collections.value];
                const container = document.querySelector('#accordion-container');
                const accordion = container.querySelector('#accordion');
                const itemTemplate = container.querySelector('#accordion-template');
                const items = accordion.querySelectorAll('.accordion-item');
    
                for (var i = 0; i < items.length; i++) {
                    items[i].remove();
                }
                
                let methods = collection.methods.map((m) => ({
                    name: setInfoTitle(m),
                    desc: m.description
                }));
                methods = [{ name: "Description", desc: collection.description }].concat(methods);
    
                methods.forEach((m, i) => {
                    const newItem = itemTemplate.cloneNode(true);
                    newItem.id = "";
                    newItem.querySelector('.accordion-title').textContent = m.name;
                    newItem.querySelector('.accordion-content').textContent = m.desc;
                    if (i === 0) {
                        newItem.classList.add('is-active');
                    }

                    accordion.appendChild(newItem);
                });

                document.querySelector('#info-title').textContent = collection.name;
                new Foundation.Accordion($(accordion), {});
                $('#info-cell').fadeIn('fast');
                chart.resize();
            });
            deleteBtn.addEventListener('click', () => {
                var id = form.id;
    
                if (form && config.forms.length > 0) {
                    form.remove();
                    
                    config.forms.splice(id, 1);
                    config.forms.forEach((f, i) => f.id = i);
                    config.next = config.forms.length;
    
                    disable(false, [addForm]);
    
                    chart.data.datasets.splice(id, 1);
                    chart.update();
                }
            });

            // GET select options
            Promise.all([
                Helper.request('api/collections', {}, (r) => r),
                Helper.request('api/operations', {}, (r) => r)
            ]).then(([c, o]) => {
                Helper.createSelectHTML(collections, c.Collections);
                operations.c = o;
                collections.value = lastCValue ? lastCValue : collections.value;
                collections.onchange();
            });
        };
        const disable = (d, els) => {
            els.forEach((e) => {
                if (d) {
                    e.setAttribute('disabled', '');
                } else {
                    e.removeAttribute('disabled');
                }
            });
        };
        const disableAll = (d) => {
            const submits = config.forms.map((f) => f.querySelector('[name=submit]'));
            const deletes = config.forms.map((f) => f.querySelector('[name=delete]'));    
            disable(d, [ ...submits, ...deletes, inputSize, testAll, dataPoint, addForm]);
            if (!d) {
                disable(config.next === config.max, [addForm]);
            }
        };
        const setRangeLabel = (label, range) => {
            label.textContent = `${label.name} (${range.value})`;
        };
        const testAllFn = () => {
            const reqs = [];
            config.forms.forEach((f) => {
                var submit = f.querySelector('[name=submit]');
                reqs.push(submit.onclick);
            });

            Promise.all(reqs.map((req) => req({ bulk: true }))).then(() => {
                chart.update();
                chart.options.animation.onComplete = () => {
                    disableAll(false);
                }
            });
        };
        const setInfoTitle = (m) => {
            const params = m.params.join(', ').replace(/java\..*\./g, '');
            return `${m.type} - ${m.bigo} - ${m.name}(${params})`;
        };

        setRangeLabel(inputSizeLabel, inputSize);
        setRangeLabel(dataPointLabel, dataPoint);

        return {
            createForm: function() {
                var id = config.next;
                var form = container.querySelector('#form-template');
                var newForm = form.cloneNode(true);

                newForm.id = id;
                container.appendChild(newForm);

                config.forms[id] = newForm;
                config.next = config.forms.length;

                disable(config.next === config.max, [addForm]);
                addEvents(newForm);
            }
        };
    })();

    Form.createForm();
})();
