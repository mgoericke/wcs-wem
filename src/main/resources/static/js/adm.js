/**
*	requires jQuery
*/

var statLinks = $('.dropdown-menu').find('a');
$(statLinks).each(function(){
	$(this).click(function(){
		
		
		
		var dataId = $(this).attr('data-id').split(':');
		var type = 'blogposts';
		if(dataId[0] == 'FW_Comment'){
			type = 'comments';
		}
		
		var uri = '/'+type+'/'+dataId[1]+'/state/'+dataId[2];
		console.log(uri);
		var href = (location.href);
		
		$.ajax({
			url:uri,
			type:'POST'
		}).done(function(data){
			
		}).fail(function(){
			console.log('ups');
		});
		
		// simply reload
		location.reload();
		
	});
});