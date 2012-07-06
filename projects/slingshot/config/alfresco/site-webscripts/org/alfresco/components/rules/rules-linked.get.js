<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">

model.rootNode = DocumentList.getConfigValue("RepositoryLibrary", "root-node", "alfresco://company/home");

// Widget instantiation metadata...
model.webScriptWidgets = [];
var rulesLinked = {};
rulesLinked.name = "Alfresco.RulesLinked";
rulesLinked.provideMessages = true;
rulesLinked.provideOptions = true;
rulesLinked.options = {};
rulesLinked.options.siteId = (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "";
rulesLinked.options.nodeRef = (page.url.args.nodeRef != null) ? page.url.args.nodeRef : "";
rulesLinked.options.repositoryBrowsing = (model.rootNode != null);
model.webScriptWidgets.push(rulesLinked);