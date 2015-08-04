/**
 * required:jquery
 */    	

var DEBUG = true;

function log(msg){
	if(DEBUG)
		console.log(msg);
}

$(document).ready(function(){

			// Embed the frame pointing to the protected page,
			// this way the login form will be displayed in this frame.
			var url = "user/welcome";
			var iframe = "<iframe id='ifr_cont_relay' type='text/html' style='overflow-x: hidden; overflow-y: hidden;' border='0' frameborder='0' width='350px' height='240px' src='" + url + "'></iframe>";
			
			$('#signinContainer').append(iframe);
			
			/*
			// load comments and display below the product
	    	$('.comments a').each(function(){
	    		
	    		var prodId = $(this).attr('prod-id')
	    		var parent = $(this).parent();
	    		
	    		$(this).click(function(evt){
	    			console.log('clicked: ' + prodId );
	    			
	    			commentsUrl = '/comments/product/' + prodId;
	    			$.ajax({
	    				url : commentsUrl
	    			}).done(function(data){
	    				parent.append(data);
	    			}).fail(function(){
	    				log('something went wrong while loading comments for product');
	    			}).always(function(){
	    				log('load comments for product');
	    			});
	    			
	    			evt.preventDefault();
	    		});
	    	});
	    	*/
			
			
    	});
    	
    	    	
    	var btns = $("button[class*=addcomment]");
    	for(var i=0; i < btns.length; i++){
    		var btn = btns[i];
    		$(btn).click(function(){
    			var frm = $(this).closest('form');
    			var relid = frm.find('input[name="relid"]').val();
    			
    			$.ajax({
                    type: frm.attr('method'),
                    url: frm.attr('action'),
                    data: frm.serialize(),
                    success: function (data) {
                        //alert('ok');
                    	
                    	var markup = '<li>';
                    	markup += '<p>';
                    	markup += '<a data-toggle="collapse" href="#comment'+data.id+'" aria-expanded="false" aria-controls="comment'+data.id+'">'+data.title+' ['+data.createdby+']</a>';
                    	markup += '</p>';
                    	markup += '<div class="collapse" id="comment'+data.id+'">';
                    	markup += '<div class="well">';
                    	markup += data.content;
                    	markup += '</div>';
                    	markup += '</div>';
                    	markup += '</li>';
                    	$('#ul' + relid).append(markup);

                    },
                    error:function(){
                    	alert('target not found');
                    }
                });
    			
    		});
    	}
    	
    	
    	