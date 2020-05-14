(function() {

    // Dom elements
    var chartEl = document.getElementById('chart');
    var collections = document.getElementById('collections');
    var operations = document.getElementById('operations');
    var inputSize = document.getElementById('inputSize');

    // Events
    collections.onchange = () => {
        if (operations.c) {
            const values = Object.keys(operations.c[collections.value]);
            setSelect(operations, values);
        }
    };

    operations.onchange = () => {
        // TODO: show description somewhere!
    };

    // Fetch API helper methods
    var FetchHelper = (function() {
        return {
            request: function(url, opts, fn) {
                fetch(new Request(url, opts)).then(function(response) {
                    response.json().then(fn).catch(function(e) {
                        alert(url + ": (" + response.status + ") " + e);
                    });
                });
            }
        };
    })();

    const setSelect = function(dom, values) {
        var option, i;

        dom.innerHTML = '';

        for (i = 0; i < values.length; i++) {
            option = document.createElement("option");
            option.setAttribute('value', values[i]);
            option.text = values[i];
            dom.appendChild(option);
        }
    };

    // Create the chart
    var chart = new Chart(chartEl, {
        type: 'line',
        data: {
            labels: [],
            datasets: [{
                label: '',
                data: [],
                borderColor: '#296dab',
                // backgroundColor: '#000000'
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
    FetchHelper.request('api/collections', {}, (r) => {
        setSelect(collections, r.Collections);
        collections.onchange();
    });
    FetchHelper.request('api/operations', {}, (r) => {
        operations.c = r;
        collections.onchange();
    });

    // POST api/collection/test
    document.getElementById('submit').addEventListener('click', function(e) {
        e.preventDefault();

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
            chart.data.datasets[0].label = collections.value;
            chart.data.datasets[0].data = [];
            for (var n in json) {
                chart.data.labels.push(n);
                chart.data.datasets[0].data.push({ x:n, y:json[n] });
            }
            chart.update();
        });
    });
})();
