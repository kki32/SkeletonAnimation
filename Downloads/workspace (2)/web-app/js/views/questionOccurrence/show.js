define(["jquery","mediator","notificationHandler","ajax"],function ($,mediator) {
   
	var page = {
			setupFreeTextFilter: function(qoccId){
				
				var timeoutId = null;
				
				
				function applyFilter(){
				    
			        var ownerFilterVal = false;
			        var globalFilterVal = false;
			        
			    if($('#owner-filter').prop('checked')){
			            ownerFilterVal = true;
			        }
			    if($('#global-filter').prop('checked')){
			        globalFilterVal = true;
			        }
			        
			        var params = {
			        url: '/questionOccurrence/updateFiltersAjax',
			            onSuccess: null,
			            data: {
			            	qoccId: qoccId,
			                textList: $('#filter-list-text').val(),
			                ownerFilter: ownerFilterVal,
			                globalFilter: globalFilterVal
			            }
			        };
			    
			        
			    
			    
			    //console.debug(params);
			    mediator.publish("ajax-json",params);
			    	timeoutId = null;
			    }
			    
				
				
			    $('.filter-list').on('keyup',function(){
			    	if(timeoutId){
			    		clearTimeout(timeoutId);
			    	}
			    	timeoutId = setTimeout(applyFilter,350);
			    });
			    $('.filter-check').on('change',applyFilter);
			}
	}
	
	return page;
    
});