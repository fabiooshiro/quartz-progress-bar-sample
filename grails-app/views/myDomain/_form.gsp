<%@ page import="quartz.progress.bar.sample.MyDomain" %>



<div class="fieldcontain ${hasErrors(bean: myDomainInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="myDomain.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${myDomainInstance?.name}"/>
</div>

