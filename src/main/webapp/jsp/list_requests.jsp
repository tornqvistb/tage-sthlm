<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%><%
%><%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%><%
%><%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%><%
%><%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%><%
%><%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %><%
%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><%
%><portlet:resourceURL id="thumbnail" cacheability="cacheLevelFull" var="thumbnailUrl"/><%
%><portlet:renderURL var="createRequestUrl"><portlet:param name="externalPage" value="createRequest"/></portlet:renderURL><%
%><portlet:defineObjects/>
<portlet:renderURL var="listMyRequestsUrl"><portlet:param name="page" value="listMyRequests"/></portlet:renderURL>

<div id="m-requested-items" class="m m-link-list">
	<div class="content-header cf">
		<a href="${listMyRequestsUrl}" class="button">Mina efterlysningar</a>
		<a href="/web/stocket/annonser?p_p_id=Retursidan_WAR_tageportlet&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&_Retursidan_WAR_tageportlet_page=createRequest" class="button primary-button">Lägg in efterlysning</a>
	</div>
	<h1>Efterlysningar</h1>
	<div class="m-h">
		<h2>Alla efterlysningar</h2>
	</div>
	<div id="request-list" class="m-c">
		<c:if test="${fn:length(requests) gt 0}">
			<ul class="inventory-listing expandible">
				<c:forEach items="${requests}" var="request">
					<c:if test="${fn:length(request.photos) gt 0}">
						<c:forEach var="photo" items="${request.photos}" end="0">
			     			<c:set var="imageUrl" value="${thumbnailUrl}&id=${photo.id}"/>
						</c:forEach>
					</c:if>
					<c:if test="${fn:length(request.photos) eq 0}">
	     				<c:set var="imageUrl" value="${thumbnailUrl}&id="/>
					</c:if>
					<c:set var="viewRequestUrl" value="/web/stocket/annonser?p_p_id=Retursidan_WAR_tageportlet&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&_Retursidan_WAR_tageportlet_page=viewRequest&_Retursidan_WAR_tageportlet_requestId=${request.id}"/>
					<li class="cf">
						<div class="image">
							<a href="${viewRequestUrl}"><img src="${imageUrl}" alt=""></a>
						</div>
										
						<div class="content">
							<div class="meta"><span class="date">${request.formattedCreatedDate}</span></div>
							<h3>
								<a href="${viewRequestUrl}">${request.title}</a>								
							</h3>
							<div class="meta">ID-nummer: <span class="highlighted-id">${request.id}</span></div>
							<div class="meta clear"><span class="author">Upplagd av ${request.unit.name}</span></div>
							<div class="meta clear"><span class="category">${request.category.parent.title} <span class="sep">&gt;</span> ${request.category.title}</span></div>
						</div>
					
					</li>
				</c:forEach>
			</ul>
		</c:if>
		<c:if test="${fn:length(requests) eq 0}">
			<div>Inga efterlysningar hittades.</div>
		</c:if>		
	</div>
</div>
<jsp:include page="include/incl_list_utils.jsp" />