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
        var master = document.getElementById('master-form');
        var addForm = master.querySelector('#addForm');
        var testAll = master.querySelector('#testAll');
        var inputSize = master.querySelector('#inputSize');
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
        inputSize.addEventListener('change', () => testAllFn());

        // private methods
        const testAllFn = () => {
            const reqs = [], fields = [];
            config.forms.forEach((f) => {
                var submit = f.querySelector('[name=submit]');
                reqs.push(submit.onclick);
                fields.push(submit);
            });

            Promise.all(reqs.map((req) => req({ bulk: true }))).then(() => {
                chart.update();
                chart.options.animation.onComplete = () => {
                    disable(false, fields.concat([testAll, inputSize]));
                }
            });
        };
        const addEvents = function(form) {
            var collections = form.querySelector('[name=collections]');
            var operations = form.querySelector('[name=operations]');
            var submit = form.querySelector('[name=submit]');
            var deleteBtn = form.querySelector("[name=delete]");
            var color = form.querySelector('[name=color]');

            // initialize fields
            const setColors = config.forms.map((f) => f.color);
            color.value = config.defaultColors.filter((c) => !setColors.includes(c))[0];
            form.color = color.value;

            // Events
            collections.onchange = () => {
                const values = operations.c[collections.value].methods.map((m) => m.type);
                Helper.createSelectHTML(operations, values);
                operations.onchange();
            };
            operations.onchange = () => {
                const methods = operations.c[collections.value].methods;
                const { type, name, description } = methods.find((m) => m.type === operations.value);
                // TODO: show description somewhere!?
                console.log(type, name, description);
            };
            color.onchange = () => {
                form.color = color.value;
            };
            submit.onclick = (e) => {
                if (submit.hasAttribute('disabled')) return;
                disable(true, [submit, inputSize, testAll]);

                return Helper.request('api/collection/test', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({
                        collection: collections.value,
                        operation: operations.value,
                        inputSize: inputSize.value
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
                            disable(false, [submit, inputSize, testAll]);
                        }
                    }
                });
            };

            deleteBtn.addEventListener('click', (e) => {
                deleteForm(parseInt(form.id));
            });

            // GET select options
            Promise.all([
                Helper.request('api/collections', {}, (r) => r),
                Helper.request('api/operations', {}, (r) => r)
            ]).then(([c, o]) => {
                Helper.createSelectHTML(collections, c.Collections);
                operations.c = o;
                collections.onchange();
            });
        };
        const deleteForm = function(id) {
            var form = config.forms[id];

            if (form && config.forms.length > 0) {
                form.remove();
                
                config.forms.splice(id, 1);
                config.forms.forEach((f, i) => f.id = i);
                config.next = config.forms.length;

                disable(false, [addForm]);

                chart.data.datasets.splice(id, 1);
                chart.update();
            }
        };
        const disable = (d, els) => {
            els.forEach((e) => {
                if (d) {
                    e.setAttribute('disabled', '');
                } else {
                    e.removeAttribute('disabled');
                }
            });
        }

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
