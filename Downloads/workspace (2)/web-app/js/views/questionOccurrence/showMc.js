// Highcharts is in the global space. It is not compatible with AMD.
define(["jquery", "views/questionOccurrence/common", "highcharts"], function($, common, Highcharts){
	/*globals $, document, setTimeout, Highcharts*/

	var chart;
	var occurrenceId = 0;
	var displayType = "";

	function myTimestamp() {
		"use strict";
		var tstmp = new Date();
		return tstmp.getTime();
	}

	/**
	 * Get which version of IE is being used, or undefined if non-IE browser
	 */
	var ie = (function (){	 
	    var undef,
	    	v = 3,
	        div = document.createElement('div'),
	        all = div.getElementsByTagName('i'); 
	    while (
	        div.innerHTML = '<!--[if gt IE ' + (++v) + ']><i></i><![endif]-->',
	        all[0]
	    ); 
	    return v > 4 ? v : undef; 
	}());

	
	function wrapString(str, maxLineLen) {
		var lnBrk = "-<br/>"
		for (var i = maxLineLen; i < str.length; i += maxLineLen) {
			str = str.substring(0, i) + lnBrk + str.substring(i);
			i += lnBrk.length
		}
		return str;
	}
	
	function breakGrouped(str) {
		return str.split(',');
	}
	
	/**
	 * Get the current chart type
	 * @returns {String}
	 */
	function getChartType() {
		var ix = window.location.hash.indexOf("#");
		if (ix === -1) {
			window.location = "#pie";
			return "pie";
		}
		switch(window.location.hash.substring(ix)) {
			case "#pie":	
				displayType = "";
				return "pie";
			case "#column":
				displayType = "";
				return "column";
			case "#columnGrouped" :
				displayType = "Grouped";
				$("#groupedOption").prop("checked", true);
				return "column";
			case "#pieGrouped":
				displayType = "Grouped";
				$("#groupedOption").prop("checked", true);
				return "pie";
			default: 
				window.location = "#pie";
				return "pie";   // Idiot has hacked at URL, give the poor bugger a pie.
		}
	}

//	/**
//	 * Rebuild the graph if the dataset structure has changed
//	 * @param data the data to build from
//	 */
//	function rebuildGraph(data) {
//		chart.series[0].remove();
//		var colors = Highcharts.getOptions().colors,
//			totalResponses = 0,
//			i = 0,
//			categories = [],
//			series = {
//				type: getChartType(),
//				data: []
//			};
//		$.each(data, function(key, val) {	
//			totalResponses += val;
//			series.data.push({				
//				name: key, 
//				y: val,
//				id: key,
//				color: colors[i++ % colors.length]
//			});
//			categories.push(key);
//		});	
//		chart.addSeries(series);
//		if (getChartType() === "column") {
//			chart.series[0].xAxis.setCategories(categories, true);
//		}
//		else {
////			chart.series[0].xAxis.enabled = false;
//		}
//	}

	/**
	 * Get new data from the server to update the chart with
	 * @param id - the questionOccurrence id
	 * @param needDataSet - boolean on whether a new dataset is required or not
	 */
	function updateChart(needDataSet) {
		"use strict";
		if(typeof needDataSet ==='boolean' && needDataSet) {
			var div = chart.options.chart.renderTo;
			chart.destroy();
			initMCChart(occurrenceId, 0, div)
		} 
		else {
			$.getJSON('/questionOccurrence/getResponsesJSON/' + occurrenceId + '?displayType=' + displayType + '&ignorecache=' + myTimestamp(), function(data) {
				$.each(data, function(key, val) {					
					if(chart.get(key) == null) {
						chart.series[0].addPoint({
							color: Highcharts.getOptions().colors[chart.series[0].data.length % Highcharts.getOptions().colors.length],
							id: key,
							name: key,
							y: val
						}, false, false, true);						
						if (getChartType() === "column") {
							chart.series[0].xAxis.categories.push(key);
						}
					} 
					else {					
						chart.get(key).update({
							y: val,
							color: chart.get(key).color
						});
					}
				});
				chart.redraw();
			});		
		}	
	}

	/**
	 * Called on init to periodically check for updates from the server.
	 * @param id - the questionOccurrences id
	 * @param version - the version of the questionOccurrence
	 */
	function refreshChart(id, version) {
		"use strict";
		var chartedVersion = version;
		function checkUpdate() {
			$.get('/questionOccurrence/getVersion/' + occurrenceId + '?ignorecache=' + myTimestamp(), function (version) {
				if (version !== chartedVersion) {
					chartedVersion = version;
					updateChart(id, false)
				}
				setTimeout(checkUpdate, 2000);
			});
		}
		checkUpdate(version);
	}


	function getChartOptions(div, chartType) {
		 var options = {
			 chart: {
				renderTo: div,
				plotBackgroundColor: null,
				plotBorderWidth: null,
				plotShadow: false,
				type: chartType,
				reflow: true,
				spacingTop: chartType === "pie" ? 0: 30,
				spacingBottom: 0,
				style: {
					overflow: 'visible'
				}
			},
			title: {
				text: "",
				style: {
					fontSize: '0px',
					position: 'relative'//,
				}
			},
			labels: {
				items: {
					style: {
						width: '20px'
					}
				}
			},
			tooltip: {
				formatter: function () {
					var type = chart.options.type !== undefined ? chart.options.type : chartType;
					var n = this.point.y, 
						t;				
					switch (type) {
					case 'pie':
						t = Math.round(this.point.y * 100 / this.percentage)
						break;
					case 'column':
						 //column doesn't have percentage of, or total responses...have to compute
						var totalResponses = 0,
							i = 0;
						while (i < chart.series[0].data.length) {
							totalResponses += chart.series[0].data[i].y;
							i += 1;
						};
						t = totalResponses;
						break;
					default:
						return "";
					}
					var choices = breakGrouped(this.point.name);
					var tip = wrapString(choices[0], 80).trim();
					for (var j = 1; j < choices.length; j++) {
						tip += ',<br/>' + wrapString(choices[j], 80).trim();
					}
					return tip + "<br/>" + n + " out of " + t + " responses";
				},
				style: {
					fontSize: '12px'
				}
			},
			plotOptions: {
				pie: {
					allowPointSelect: false,
					animation: true,
//					cursor: 'pointer',
					dataLabels: {
						enabled: true,
//							color: '#ff0000',
//							connectorColor: '#ff0000',
//						style: {
//							visibility: 'visible',
//							overflow: 'visible'
//						},
						formatter: function () {
							var name = this.point.name;
							if (name.length > 30) {
								name = name.substring(0, 27) + "...";
							}
//							return name + " " + this.percentage.toFixed(2) + ' %'; //'<b>' + this.point.name + '</b>: ' + this.percentage.toFixed(2) + ' %';
							return this.percentage.toFixed(2) + '%<br/>' + name;
						}
					},
					showInLegend: false,
					enableMouseTracking: true,
//					stickyTracking: false,
					point: {
						events: {
							mouseOver: function () {
								this.slice(true, true, true);								
							},
							mouseOut: function () {
								this.slice(this.selected, true, true);
							},
							click: function () {
								this.select(!this.selected, false); // second param is for allowing accumulative select
							},
							select: function () {
								this.slice(true, true, true);
							},
							legendItemClick: function() {
								this.select(!this.selected, false);
								return false;
							},						
						}
					},
				},
				column: {
					enableMouseTracking: true,
					cursor: 'pointer',
					dataLabels: {
						enabled: false,
						style: {
//							color: '#ff0000',
							fontSize: '16px',
							fontWeight: 'bold'
						},
						formatter: function () {
							return breakGrouped(this.point.name);
						},
					},
					showInLegend: false
				}
			},
			yAxis: {
				allowDecimals: false,
				min: 0,
				title: {
					text: 'Number of responses',
					style: {
						fontSize: '16px'
					}
				}
			},
			xAxis: {
				allowDecimals: false,
				categories: [],
				labels: {
					style: {
						fontSize: '12px'
					},
					enabled: true,
					formatter: 	function () {
						return breakGrouped(this.value.toString());
					},			
				}
			},
			legend: [],
			exporting: {
				enabled: false,
//				filename: title.replace(/ /gi, "-"),
//				buttons: {
//					exportButton: {
//						enabled: true
//					},
//					printButton: {
//						enabled: true
//				    }
//				},
//				width: '2000px'
			},
			series: []
		};
		 return options;
	}

	/**
	 * Change between chart types
	 * @param newType - the new type to change to - one of "pie", "column"
	 */
	function changeChartType(newType) {
		"use strict";
		
		if ($(".chartDiv").css('opacity') !== '1') {
			common.toggleChartVisibility();
		}
		
		var currentType = getChartType();
		if (currentType === newType) {
			return;
		}
		window.location = '#' + newType + displayType;
		
		var series = {
				type: newType,
				data: []
			},
			categories = [],
			points = chart.series[0].data,
			i = 0;
				
		while (i < points.length) {
			series.data[i] = {
				name: points[i].name,
				y: points[i].y,
				id: points[i].id,
				color: points[i].color
			};
			categories.push(points[i].name);
			i += 1;		
		}
		var div = chart.options.chart.renderTo,
			options = getChartOptions(div, newType);
		options.xAxis.categories = categories;
		options.series.push(series);

		chart.destroy();
		chart = new Highcharts.Chart(options);
		chart.redraw();		
	}
	$("#pieBtn").on("click", function(){
		changeChartType('pie');
	});
	$("#columnBtn").on("click", function(){
		changeChartType('column');
	});

	/**
	 * Get the data to populate the chart from the server
	 * @param chartType - the type of chart
	 * @param id - the questionOccurrence id
	 * @param version - the questionOccurrence version
	 */
	function getData(chartType, options, version) {
		$.getJSON('/questionOccurrence/getResponsesJSON/' + occurrenceId + '?displayType=' + displayType , function(data) {
			var series = {
				type: chartType,
				//name: 'whatever',
				data: []
			};		
			var i = 0,
			colors = Highcharts.getOptions().colors,
			totalResponses = 0,
			categories = [];
			$.each(data, function(key, val) {	
				totalResponses += val;
				series.data.push({				
					name: key, 
					y: val,
					id: key,
					color: colors[i++ % colors.length]
				});					
				categories.push(key);
			});
			options.xAxis.categories = categories;
			options.series.push(series);
			chart = new Highcharts.Chart(options);
			refreshChart(version);
		});
	}

	/**
	 * Initialise the chart. Sets up configuration options
	 * @param id - the questionOccurrence id
	 * @param version - the questionOccurence version
	 * @param div - html div element on which to draw the chart
	 * @param title - the charts title ie the question
	 * @param chartType - the type of chart, one of "pie", "column"
	 */
	function initMCChart(theId, version, div) {
		"use strict";	
		occurrenceId = theId;
		var chartType = getChartType(),
			options = getChartOptions(div, chartType),
			chart = getData(chartType, options, version);  // need to draw the chart inside the ajax call	
	}

	/**
	 * Switch whether the choices are grouped.
	 */
	function switchDisplayGrouped() {
		if(displayType == "") {
			window.location = window.location.hash + "Grouped";
			displayType = "Grouped";
		} else {
			displayType = "";
			window.location = window.location.hash.substring(0, window.location.hash.indexOf('G'));
		}
		updateChart(true);
	}
	$("#groupedOption").on("click", switchDisplayGrouped);
	return {
		initMCChart: initMCChart
	}
});

