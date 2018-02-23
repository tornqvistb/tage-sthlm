<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%
%><%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%><%
%><%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%><%
%><%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%><%
%><%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %><%
%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><%
%><portlet:defineObjects/>

<script type="text/javascript">
$('ul.expandible').each(function(){
    var $ul = $(this),
        $lis = $ul.find('li:gt(9)'),
        isExpanded = $ul.hasClass('expanded');
    $lis[isExpanded ? 'show' : 'hide']();
    
    if($lis.length > 0){
        $ul
            .append($('<li class="expand"><span>' + (isExpanded ? 'Visa färre' : 'Visa fler') + '</span></li>')
            //.append($('<li class="expand"><button id="expanded-button">' + (isExpanded ? 'Visa färre' : 'Visa fler') + '</button></li>')
            .click(function(event){
                var isExpanded = $ul.hasClass('expanded');
                event.preventDefault();
                $(this).text(isExpanded ? 'Visa fler' : 'Visa färre');
                $ul.toggleClass('expanded');
                $lis.toggle();
            }));
    }
});
</script>
<style type="text/css">
	li.expand{
		text-align: center;
		background: #ffd7d2;
    	color: #dd4a2c;
	    border: none;
	    border-radius: 5px;
	    margin-left: 5px;
	    margin-top: 5px;
	    cursor: pointer;
	}
</style>
 