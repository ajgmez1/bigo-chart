(function() {

    // Dom elements
    var chartEl = document.getElementById('chart');
    var collections = document.getElementById('collections');
    var operations = document.getElementById('operations');
    var inputSize = document.getElementById('inputSize');

    // Fetch API helper methods
    var FetchHelper = (function() {
        return {
            request: function(url, opts, fn) {
                fetch(new Request(url, opts)).then(function(response) {
                    response.json().then(fn).catch(function(e) {
                        alert(url + ": (" + response.status + ") " + e);
                    });
                });
            },
            processGetRequest: function(dom) {
                return function(json) {
                    var option, i,
                        values = json.EnumValues;

                    for (i = 0; i < values.length; i++) {
                        option = document.createElement("option");
                        option.setAttribute('value', values[i]);
                        option.text = values[i];
                        dom.appendChild(option);
                    }
                };
            }
        };
    })();

    // Create the chart
    var chart = new Chart(chartEl, {
        type: 'line',
        data: {
            labels: ['1', '2'],
            datasets: [{
                label: 'Performance',
                data: [{x:1, y:2}, {x:2, y:3}],
                borderColor: 'rgb(41, 109, 171)'
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

    // GET select options
    FetchHelper.request('api/collections', {}, FetchHelper.processGetRequest(collections));
    FetchHelper.request('api/operations', {}, FetchHelper.processGetRequest(operations));

    // POST api/collection/test
    document.getElementById('submit').addEventListener('click', function(e) {
        e.preventDefault();
        if (inputSize.checkValidity()) {
            FetchHelper.request('api/collection/test', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({
                    collection: collections.value,
                    operation: operations.value,
                    inputSize: inputSize.value
                })
            }, function(json) {
                chart.data.labels = [];
                chart.data.datasets[0].data = [];
                for (var n in json) {
                    chart.data.labels.push(n);
                    chart.data.datasets[0].data.push({ x:n, y:json[n] });
                }
                chart.update();
            });
        } else {
            inputSize.reportValidity();
        }
    });
})();
