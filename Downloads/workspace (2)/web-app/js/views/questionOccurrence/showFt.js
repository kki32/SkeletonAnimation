define(["jquery", "tagcanvas"], function($, TagCanvas){
	
	/*globals $, jQuery, document, window, TagCanvas, setTimeout*/

	/**
	 * Get the current time, used in ajax calls to prevent ie being a twat
	 * @returns
	 */

	//var displayType;
	//var chartId;
	var showFt= {};
	function myTimestamp() {
		"use strict";
	    var tstmp = new Date();
	    return tstmp.getTime();
	}

	/**
	 * Get which version of IE is being used, or undefined if non-IE browser
	 */
	var ie = (function(){	 
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


	function drawStaticCloud(id, filters) {
		$("#cloud").fadeOut("fast", function() {
			$("#cloud").attr('src', '/questionOccurrence/getResponsesImage/' + id + '?displayType=cloud' + '&filters=' + filters + '&ignorecache=' + myTimestamp());
		});
		$("#cloud").fadeIn("fast");
	}

	/**
	 * Draw an animated word cloud
	 * @param id - the questionOccurrence id
	 */
	function drawAnimatedCloud(id, filters) {
		"use strict";
		jQuery.get('/questionOccurrence/getResponsesJSON/' + id + '?displayType=animCloud' + '&filters=' + filters + '&ignorecache=' + myTimestamp(), function (map) {
			if (map.length) {
				var maxSz = 24,minSz = 6, maxWeight = map[0].weight, options = {
					textColour: '#0000ff',
					outlineColour: '#ff0000',
					outlineMethod: "outline",
					outlineThickness: 1,
					outlineOffset: 0,
//						pulsateTo: .1,
//						pulsateTime: 2,
					frontSelect: true,
					minBrightness: 0.2,
					weight: true,				
					weightMode: (ie < 9) ? "size" : "both" ,
//						weightFrom: "weight",
					weightGradient: {
						0: 'rgba(255,0,0,220)',
						1: 'rgba(0,0,255,50)'
					},
					weightSize: 1,
//					shadow: 'rgba(10,10,10,10)',
//					shadowBlur: 2,
//					shadowOffset: [1,1],
					maxSpeed: (ie < 9) ? 0.15 : 0.04,
					decel: 0.8,
					interval: (ie < 9) ? 40 : 20,		
					freezeActive: true,
					reverse: false,
					zoom: 1.0,
					wheelZoom: false,
					zoomMin: 0.7,
					zoomMax: 1.0,
					depth: 0.3
				};			
				
				$.each(map, function (index, word) {
					var sz = word.weight / maxWeight * (maxSz - minSz) + minSz;		
					var txt = "shizx"
					$("#tags").append('<a href="" id=word-' + index + ' class="word" title="ggg"  style="font-size: ' + sz + 'pt"' + sz + '">' + word.text + '</a>');
					
					$("#word-" + index).on('click', function(e) {	
						alert(e.currentTarget.text)
						return false;
					});
				});
				jQuery(document).ready(function () {
					TagCanvas.Start("cloudCanvas", "tags", options);
				});
			}
		});
	}
	
	/**
	 * 
	 * @param id - the questionOccurrence id
	 * @param version - the questionOccurrence version
	 * @param type - the chart type
	 */
	showFt.initCloud = function(id, version, type, filters) {
		"use strict";
		var chartedVersion = version;

		function checkUpdate() {
			$.get('/questionOccurrence/getVersion/' + id + '?ignorecache=' + myTimestamp(), function (version) {
				if (version !== chartedVersion) {
					chartedVersion = version;
					if (type === "cloud") {
						drawStaticCloud(id, filters);
					} else if (type === "animCloud") {
						$(".word").remove();
						drawAnimatedCloud(id, filters);
					}
				}
				setTimeout(checkUpdate, 5000);
			});
		}
		checkUpdate();
	}

	/**
	 * Change between chart types/filter levels
	 * @param newType - the type to change to. One of "rawFT", "cloud", "animCloud"
	 * @param id - the questionOccurrence id
	 */
	function setChartType(newType, id, filters) {
		"use strict";
		window.location.replace("/questionOccurrence/show/" + showFt.questionOccurrenceId + "?displayType=" + newType + "&filters=" + showFt.filters);
	}

	$('#showRawFTBtn').on('click', function(){setChartType('rawFT')});
	$('#showCloudFTBtn').on('click', function(){setChartType('cloud')});
	$('#showAnimCloudFTBtn').on('click', function(){setChartType('animCloud')});



	return showFt;
})