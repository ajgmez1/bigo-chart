(function() {

    // helper methods
    var Helper = (function() {
        return {
            request: function(url, opts, fn) {
                fetch(new Request(url, opts)).then(function(response) {
                    response.json().then(fn).catch(function(e) {
                        console.error(url, response.status, e);
                    });
                });
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

    // global events
    document.getElementById('addForm').addEventListener('click', function(e) {
        e.preventDefault();
        Form.createForm();
    });

    var Form = (function() {
        var inputSize = document.querySelector('[name=inputSize]');
        var container = document.getElementById('container');
        var ids = {
            next: 1,
            max: 5,
            forms: []
        };

        const addEvents = function(form) {
            var id = form.id;
            var collections = form.querySelector('[name=collections]');
            var operations = form.querySelector('[name=operations]');
            var color = form.querySelector('[name=color]');

            // Events
            collections.onchange = () => {
                if (operations.c) {
                    const values = operations.c[collections.value].methods.map((m) => m.type);
                    Helper.createSelectHTML(operations, values);
                    operations.onchange();
                }
            };

            operations.onchange = () => {
                if (operations.c) {
                    const methods = operations.c[collections.value].methods;
                    const { type, name, description } = methods.find((m) => m.type === operations.value);
                    // TODO: show description somewhere!
                    console.log(type, name, description);
                }
            };
            // POST api/collection/test
            form.querySelector('[name=submit]').addEventListener('click', function(e) {
                e.preventDefault();

                Helper.request('api/collection/test', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({
                        collection: collections.value,
                        operation: operations.value,
                        inputSize: inputSize.value
                    })
                }, function(json) {
                    chart.data.labels = [];
                    chart.data.datasets[id] = {};
                    chart.data.datasets[id].label = collections.value + ' - ' + operations.value;
                    chart.data.datasets[id].borderColor = color.value;
                    chart.data.datasets[id].data = [];
                    for (var n in json) {
                        chart.data.labels.push(n);
                        chart.data.datasets[id].data.push({ x:n, y:json[n] });
                    }
                    chart.update();
                });
            });

            form.querySelector("[name=delete]").addEventListener('click', function(e) {
                e.preventDefault();
                deleteForm(parseInt(id));
            });

            // GET select options
            Helper.request('api/collections', {}, (r) => {
                Helper.createSelectHTML(collections, r.Collections);
                collections.onchange();
            });
            Helper.request('api/operations', {}, (r) => {
                operations.c = r;
                collections.onchange();
            });
        };
        const deleteForm = function(id) {
            var forms = container.querySelectorAll('form');
            var form = ids.forms[id];

            if (form && forms.length > 1) {
                form.remove();
                ids.forms[id] = false;
                if (id < ids.next) {
                    ids.next = id;
                }
                chart.data.datasets.splice(id, 1);
                chart.update();
            }
            
            console.warn(ids);
        };

        // add first form events
        const form = container.querySelector('form');
        ids.forms[0] = form;
        addEvents(form);

        return {
            createForm: function() {
                var id = ids.next;
                var form = container.querySelector('form');
                
                if (id < ids.max) {
                    var newForm = form.cloneNode(true);
                    newForm.id = id;
                    container.appendChild(newForm);

                    ids.forms[id] = newForm;
                    var newId = ids.forms.findIndex((v) => !v);
                    if (newId === -1) {
                        ids.next = ids.forms.length;
                    } else {
                        ids.next = newId;
                    }

                    addEvents(newForm);
                }
                
                console.warn(ids);
            }
        };
    })();
})();
