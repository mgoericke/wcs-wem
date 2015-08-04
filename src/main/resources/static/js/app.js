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
    	
    	
    	
    	
        var links = $('.btn-group').find('a'); 
        $(links).each(function(item){
        	
        	$(this).click(function(){
            	var state = $(this).attr('href').substring(1, $(this).attr('href').length);
                loadComments(state);
                
        	});
        });
        
        function loadComments(state){
        	var controller = '/comments/state/' + state;
            $.ajax({
            	url: controller
            }).then(function(data){
            	var comments = $('#comments');
            	
            	$(comments).empty();
            	$.each(data, function(idx, comment){            		
                	$(comments).append(getCommentMarkup(comment));
            	});
            	$(comments).fadeIn();
            });
        }
        
        function getCommentMarkup(comment){
        	var markup = '<div class="media">';
    		markup += '<div class="media-body">';
    		markup += '<div class="media-heading smallertext"><span class="' +comment.state+ 'State">' +comment.state+ '</span> | Erstellt am: ' + comment.createddate + ' | Erstellt von: ' + comment.createdby + ' | Rating: ' + comment.rating + '</div>';
    		markup += comment.title;
    		markup += '<div class="commentfooter smallertext">Kommentar f&uumlr: <a href="#">' +comment.contentAsset.title+ '</a></div>';
    		markup += '</div>';
    		markup += '<div class="media-right">';
    		markup += '<a href="#">';
    		markup += '<img class="media-object" data-src="4" alt="avatar" src="/wem-community/static/img/avatar.jpeg" data-holder-rendered="true" style="width: 64px; height: 64px;">';
    		markup += '</a>';
    		markup += '</div>';
    		markup += '</div>';	
    		return markup;
        }
        
        $(document).ready(function(){
        	loadComments('all');
        });
    	
    	