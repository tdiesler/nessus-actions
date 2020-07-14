(function (factory) {
    typeof define === 'function' && define.amd ? define(factory) :
    factory();
}((function () { 'use strict';

    var __extends = (undefined && undefined.__extends) || (function () {
        var extendStatics = function (d, b) {
            extendStatics = Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
                function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
            return extendStatics(d, b);
        };
        return function (d, b) {
            extendStatics(d, b);
            function __() { this.constructor = d; }
            d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
        };
    })();
    var __makeTemplateObject = (undefined && undefined.__makeTemplateObject) || function (cooked, raw) {
        if (Object.defineProperty) { Object.defineProperty(cooked, "raw", { value: raw }); } else { cooked.raw = raw; }
        return cooked;
    };
    var RHDPProjectFilterBox = (function (_super) {
        __extends(RHDPProjectFilterBox, _super);
        function RHDPProjectFilterBox() {
            var _this = _super.call(this) || this;
            _this._term = '';
            _this._filter = '';
            _this.template = function (strings, project) {
                return "\n        <form action=\"\" class=\"project-filters\" method=\"GET\" data-drupal-form-fields=\"\">\n            <h4>Filters<a class=\"project-filters-clear\" href=\"#\">Clear All Filters</a></h4>\n            <input name=\"filter-text\" placeholder=\"Filter by keyword\" type=\"text\" value=\"" + project.term + "\">\n            <div class=\"filter-block\">\n                <h5>Included In</h5>\n        \n                <div class=\"styled-select\" ><select name=\"filter-products\" id=\"upstream-project-selection\">\n                    <option value=\"\">Select Product...</option>\n                    <option value=\"amq\">Red Hat JBoss AMQ</option>\n                    <option value=\"rhpam\">Red Hat Process Automation Manager</option>\n                    <option value=\"brms\">Red Hat Decision Manager</option>\n                    <option value=\"datagrid\">Red Hat JBoss Data Grid</option>\n                    <option value=\"datavirt\">Red Hat JBoss Data Virtualization</option>\n                    <option value=\"devstudio\">Red Hat JBoss Developer Studio</option>\n                    <option value=\"eap\">Red Hat JBoss Enterprise Application Platform</option>\n                    <option value=\"fuse\">Red Hat JBoss Fuse</option>\n                    <option value=\"rhel\">Red Hat Enterprise Linux</option>\n                    <option value=\"webserver\">Red Hat JBoss Web Server</option>\n                </select></div>\n            </div>\n        </form>\n";
            };
            return _this;
        }
        Object.defineProperty(RHDPProjectFilterBox.prototype, "filter", {
            get: function () {
                return this._filter;
            },
            set: function (value) {
                this._filter = decodeURI(value);
                var filterAttrib = this.querySelector('select[name="filter-products"]');
                if (value === "") {
                    filterAttrib.selectedIndex = 0;
                }
                else {
                    filterAttrib.setAttribute('value', this.filter);
                }
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectFilterBox.prototype, "term", {
            get: function () {
                return this._term;
            },
            set: function (value) {
                this._term = decodeURI(value);
                this.querySelector('input').value = this.term;
            },
            enumerable: true,
            configurable: true
        });
        RHDPProjectFilterBox.prototype.connectedCallback = function () {
            var _this = this;
            this.innerHTML = this.template(templateObject_1 || (templateObject_1 = __makeTemplateObject(["", ""], ["", ""])), this);
            this.addEventListener('submit', function (e) {
                e.preventDefault();
                _this._filterChange(e);
            });
            this.querySelector('select[name="filter-products"]').addEventListener('change', function (e) {
                e.preventDefault();
                _this._filterChange(e);
            });
            this.querySelector('.project-filters-clear').addEventListener('click', function (e) {
                e.preventDefault();
                _this._clearFilters(e);
            });
        };
        RHDPProjectFilterBox.prototype._clearFilters = function (e) {
            e.preventDefault();
            this.filter = "";
            this.term = "";
            this._updateProjectFilters();
        };
        RHDPProjectFilterBox.prototype._filterChange = function (e) {
            if (e.currentTarget.id == "upstream-project-selection") {
                this.filter = e.currentTarget.value;
            }
            this.term = this.querySelector('input').value;
            this._updateProjectFilters();
        };
        RHDPProjectFilterBox.prototype._updateProjectFilters = function () {
            this.dispatchEvent(new CustomEvent('project-filter-change', {
                detail: {
                    filter: this.filter,
                    term: this.term
                },
                bubbles: true
            }));
        };
        Object.defineProperty(RHDPProjectFilterBox, "observedAttributes", {
            get: function () {
                return ['loading'];
            },
            enumerable: true,
            configurable: true
        });
        RHDPProjectFilterBox.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
        };
        return RHDPProjectFilterBox;
    }(HTMLElement));
    window.customElements.define('rhdp-project-filter-box', RHDPProjectFilterBox);
    var templateObject_1;

    var __extends$1 = (undefined && undefined.__extends) || (function () {
        var extendStatics = function (d, b) {
            extendStatics = Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
                function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
            return extendStatics(d, b);
        };
        return function (d, b) {
            extendStatics(d, b);
            function __() { this.constructor = d; }
            d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
        };
    })();
    var __makeTemplateObject$1 = (undefined && undefined.__makeTemplateObject) || function (cooked, raw) {
        if (Object.defineProperty) { Object.defineProperty(cooked, "raw", { value: raw }); } else { cooked.raw = raw; }
        return cooked;
    };
    var RHDPProjectItem = (function (_super) {
        __extends$1(RHDPProjectItem, _super);
        function RHDPProjectItem() {
            var _this = _super.call(this) || this;
            _this.template = function (strings, project) {
                return "\n        \n            <div class=\"defaultprojectimage\">\n                <p class=\"image-link\"><img src=\"" + project.imageUrl + "\" alt=\"" + project.projectName + "\"></p></div>\n            <h5 class=\"solution-name\">\n                <p class=\"solution-name-link\">" + project.projectName + "</p>\n            </h5>\n            <p>\n        \n            </p>\n            <a class=\"solution-overlay-learn link-sm\">Learn more</a> " + (project.downloadsLink ? "| <a href=\"" + project.downloadsLink + "\" class=\"link-sm\">Download</a>" : '') + "\n            <div class=\"project-content row\">\n                <div class=\"large-6 project-content-left columns\"><img\n                        src=\"" + project.imageUrl + "\" alt=\"" + project.projectName + "\">\n                    <p><a class=\"upstream-download\" href=\"" + project.downloadsLink + "\"><i class=\"fa fa-download\"></i> Download</a></p>\n                    <p>\n                        " + (project.sys_url_view ? "<a href=\"" + project.sys_url_view + "\">Visit home page</a>" : '') + "\n                    </p>\n                    <ul class=\"project-social\"> \n                        " + (project.twitterLink ? "<li><a href=\"" + project.twitterLink + "\"><i class=\"fa fa-twitter\"></i></a></li>" : '') + "\n                    </ul>\n                </div>\n                <div class=\"large-18 project-content-right columns\"><h3><a href=\"" + project.sys_url_view + "\">" + project.projectName + "</a>\n                </h3>\n                    <p>" + project.descriptions + "</p>\n                    <div class=\"upstream-more-content\">\n                        <ul class=\"project-details-list\">\n                            " + (project.docsLink ? "<li>Docs: <a href=\"" + project.docsLink + "\">Documentation</a></li>" : '') + "\n                            " + (project.communityLink ? "<li>Community: <a href=\"" + project.communityLink + "\">" + project.generateViewLink(project.communityLink) + " <i class=\"fas fa-external-link\"></a></li>" : '') + "\n                            " + (project.mailingListLink ? "<li>Mailing List: <a href=\"" + project.mailingListLink + "\">" + project.generateViewLink(project.mailingListLink) + " <i class=\"fas fa-external-link\"></a></li>" : '') + "\n                            " + (project.chatLink ? "<li>Chat: <a href=\"" + project.chatLink + "\">" + project.generateViewLink(project.chatLink) + " <i class=\"fas fa-external-link\"></a></li>" : '') + "\n                            " + (project.jiraLink ? "<li>JIRA: <a href=\"" + project.jiraLink + "\">" + project.generateViewLink(project.jiraLink) + " <i class=\"fas fa-external-link\"></a></li>" : '') + "\n                            " + (project.srcLink ? "<li>Source: <a href=\"" + project.srcLink + "\">" + project.generateViewLink(project.srcLink) + " <i class=\"fas fa-external-link\"></a></li>" : '') + "\n                            " + (project.githubLink ? "<li>Github: <a href=\"" + project.githubLink + "\">" + project.generateViewLink(project.githubLink) + " <i class=\"fas fa-external-link\"></a></li>" : '') + "\n                            " + (project.buildLink ? "<li>Build: <a href=\"" + project.buildLink + "\">" + project.generateViewLink(project.buildLink) + " <i class=\"fas fa-external-link\"></a></li>" : '') + "\n                            " + (project.issueTracker ? "<li>Issue: <a href=\"" + project.issueTracker + "\">" + project.generateViewLink(project.issueTracker) + " <i class=\"fas fa-external-link\"></a></li>" : '') + "\n                            " + (project.userForumLink ? "<li>User Forum: <a href=\"" + project.userForumLink + "\">" + project.generateViewLink(project.userForumLink) + " <i class=\"fas fa-external-link\"></a></li>" : '') + "  \n                            " + (project.devForumLink ? "<li>Dev Forum: <a href=\"" + project.devForumLink + "\">" + project.generateViewLink(project.devForumLink) + " <i class=\"fas fa-external-link\"></a></li>" : '') + "  \n                            " + (project.knowledgebaseLink ? "<li>KnowledgeBase: <a href=\"" + project.knowledgebaseLink + "\">" + project.generateViewLink(project.knowledgebaseLink) + " <i class=\"fas fa-external-link\"></a></li>" : '') + " \n                            " + (project.blogLink ? "<li>Blog: <a href=\"" + project.blogLink + "\">" + project.generateViewLink(project.blogLink) + " <i class=\"fas fa-external-link\"></a></li>" : '') + " \n                            " + (project.anonymousLink ? "<li>Anonymous Source: <a href=\"" + project.anonymousLink + "\">" + project.generateViewLink(project.anonymousLink) + " <i class=\"fas fa-external-link\"></a></li>" : '') + " \n                        </ul>\n                    </div>\n                </div>\n            </div>\n        ";
            };
            return _this;
        }
        Object.defineProperty(RHDPProjectItem.prototype, "userForumLink", {
            get: function () {
                return this._userForumLink;
            },
            set: function (value) {
                value = this.getCorrectUrl(value);
                if (this._userForumLink === value)
                    return;
                this._userForumLink = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "devForumLink", {
            get: function () {
                return this._devForumLink;
            },
            set: function (value) {
                value = this.getCorrectUrl(value);
                if (this._devForumLink === value)
                    return;
                this._devForumLink = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "mailingListLink", {
            get: function () {
                return this._mailingListLink;
            },
            set: function (value) {
                value = this.getCorrectUrl(value);
                if (this._mailingListLink === value)
                    return;
                this._mailingListLink = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "chatLink", {
            get: function () {
                return this._chatLink;
            },
            set: function (value) {
                value = this.getCorrectUrl(value);
                if (this._chatLink === value)
                    return;
                this._chatLink = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "blogLink", {
            get: function () {
                return this._blogLink;
            },
            set: function (value) {
                value = this.getCorrectUrl(value);
                if (this._blogLink === value)
                    return;
                this._blogLink = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "jiraLink", {
            get: function () {
                return this._jiraLink;
            },
            set: function (value) {
                value = this.getCorrectUrl(value);
                if (this._jiraLink === value)
                    return;
                this._jiraLink = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "srcLink", {
            get: function () {
                return this._srcLink;
            },
            set: function (value) {
                value = this.getCorrectUrl(value);
                if (this._srcLink === value)
                    return;
                this._srcLink = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "anonymousLink", {
            get: function () {
                return this._anonymousLink;
            },
            set: function (value) {
                value = this.getCorrectUrl(value);
                if (this._anonymousLink === value)
                    return;
                this._anonymousLink = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "commiterLink", {
            get: function () {
                return this._commiterLink;
            },
            set: function (value) {
                value = this.getCorrectUrl(value);
                if (this._commiterLink === value)
                    return;
                this._commiterLink = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "fisheyeLink", {
            get: function () {
                return this._fisheyeLink;
            },
            set: function (value) {
                value = this.getCorrectUrl(value);
                if (this._fisheyeLink === value)
                    return;
                this._fisheyeLink = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "viewvcLink", {
            get: function () {
                return this._viewvcLink;
            },
            set: function (value) {
                value = this.getCorrectUrl(value);
                if (this._viewvcLink === value)
                    return;
                this._viewvcLink = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "committerGitLink", {
            get: function () {
                return this._committerGitLink;
            },
            set: function (value) {
                value = this.getCorrectUrl(value);
                if (this._committerGitLink === value)
                    return;
                this._committerGitLink = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "buildLink", {
            get: function () {
                return this._buildLink;
            },
            set: function (value) {
                value = this.getCorrectUrl(value);
                if (this._buildLink === value)
                    return;
                this._buildLink = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "hudsonLink", {
            get: function () {
                return this._hudsonLink;
            },
            set: function (value) {
                value = this.getCorrectUrl(value);
                if (this._hudsonLink === value)
                    return;
                this._hudsonLink = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "knowledgebaseLink", {
            get: function () {
                return this._knowledgebaseLink;
            },
            set: function (value) {
                value = this.getCorrectUrl(value);
                if (this._knowledgebaseLink === value)
                    return;
                this._knowledgebaseLink = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "communityLink", {
            get: function () {
                return this._communityLink;
            },
            set: function (value) {
                value = this.getCorrectUrl(value);
                if (this._communityLink === value)
                    return;
                this._communityLink = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "imageUrl", {
            get: function () {
                return this._imageUrl;
            },
            set: function (value) {
                value = this.getCorrectUrl(value);
                if (this._imageUrl === value)
                    return;
                this._imageUrl = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "projectName", {
            get: function () {
                return this._projectName;
            },
            set: function (value) {
                if (this._projectName === value)
                    return;
                this._projectName = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "downloadsLink", {
            get: function () {
                return this._downloadsLink;
            },
            set: function (value) {
                this.getCorrectUrl(value);
                if (this._downloadsLink === value)
                    return;
                this._downloadsLink = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "sys_url_view", {
            get: function () {
                return this._sys_url_view;
            },
            set: function (value) {
                value = this.getCorrectUrl(value);
                if (this._sys_url_view === value)
                    return;
                this._sys_url_view = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "twitterLink", {
            get: function () {
                return this._twitterLink;
            },
            set: function (value) {
                value = this.getCorrectUrl(value);
                if (this._twitterLink === value)
                    return;
                this._twitterLink = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "descriptions", {
            get: function () {
                return this._descriptions;
            },
            set: function (value) {
                if (this._descriptions === value)
                    return;
                this._descriptions = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "docsLink", {
            get: function () {
                return this._docsLink;
            },
            set: function (value) {
                value = this.getCorrectUrl(value);
                if (this._docsLink === value)
                    return;
                this._docsLink = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "issueTracker", {
            get: function () {
                return this._issueTracker;
            },
            set: function (value) {
                value = this.getCorrectUrl(value);
                if (this._issueTracker === value)
                    return;
                this._issueTracker = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectItem.prototype, "githubLink", {
            get: function () {
                return this._githubLink;
            },
            set: function (value) {
                value = this.getCorrectUrl(value);
                if (this._githubLink === value)
                    return;
                this._githubLink = value;
            },
            enumerable: true,
            configurable: true
        });
        RHDPProjectItem.prototype.getCorrectUrl = function (url) {
            if (url == null)
                return;
            if (url.constructor === Array && url.length > 0) {
                url = url[0];
            }
            if (url.indexOf("/") > 0) {
                return url;
            }
            else {
                return "https://developers.redhat.com" + url;
            }
        };
        RHDPProjectItem.prototype.connectedCallback = function () {
            this.innerHTML = this.template(templateObject_1$1 || (templateObject_1$1 = __makeTemplateObject$1(["", ""], ["", ""])), this);
        };
        RHDPProjectItem.prototype.getTemplateHTML = function () {
            this.innerHTML = this.template(templateObject_2 || (templateObject_2 = __makeTemplateObject$1(["", ""], ["", ""])), this);
            return this.innerHTML;
        };
        RHDPProjectItem.prototype.generateViewLink = function (viewLink) {
            return viewLink.replace(/https?:\/\//, '');
        };
        Object.defineProperty(RHDPProjectItem, "observedAttributes", {
            get: function () {
                return ['type', 'size', 'heading', 'text'];
            },
            enumerable: true,
            configurable: true
        });
        RHDPProjectItem.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
            this.innerHTML = this.template(templateObject_3 || (templateObject_3 = __makeTemplateObject$1(["", ""], ["", ""])), this);
        };
        return RHDPProjectItem;
    }(HTMLElement));
    window.customElements.define('rhdp-project-item', RHDPProjectItem);
    var templateObject_1$1, templateObject_2, templateObject_3;

    var __extends$2 = (undefined && undefined.__extends) || (function () {
        var extendStatics = function (d, b) {
            extendStatics = Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
                function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
            return extendStatics(d, b);
        };
        return function (d, b) {
            extendStatics(d, b);
            function __() { this.constructor = d; }
            d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
        };
    })();
    var RHDPProjectQuery = (function (_super) {
        __extends$2(RHDPProjectQuery, _super);
        function RHDPProjectQuery() {
            var _this = _super.call(this) || this;
            _this._dcpUrl = 'https://dcp2.jboss.org/v2/rest/search/suggest_project_name_ngram_more_fields?sort=sys_title&query=';
            _this._term = '';
            _this._mockData = false;
            _this.productData = {
                "amq": { "upstream": ["activemq", "fabric8"] },
                "rhpam": { "upstream": ["drools", "guvnor", "optaplanner", "jbpm"] },
                "brms": { "upstream": ["optaplanner", "drools", "guvnor"] },
                "datagrid": { "upstream": ["infinispan", "jgroups", "hibernate_subprojects_search"] },
                "datavirt": { "upstream": ["teiid", "teiiddesigner", "modeshape"] },
                "devstudio": { "upstream": ["jbosstools"] },
                "eap": { "upstream": ["wildfly", "jgroups", "hibernate", "hornetq", "jbossclustering", "jbossmc", "narayana", "jbossweb", "jbossws", "ironjacamar", "jgroups", "mod_cluster", "jbossas_osgi", "jbosssso", "picketlink", "resteasy", "weld", "wise", "xnio"] },
                "fuse": { "upstream": ["camel", "karaf", "wildfly-camel", "cxf", "syndesis", "apicurio", "hawtio"] },
                "rhel": { "upstream": ["fedora"] },
                "webserver": { "upstream": ["tomcat", "httpd", "mod_cluster"] },
            };
            _this._filterChange = _this._filterChange.bind(_this);
            return _this;
        }
        Object.defineProperty(RHDPProjectQuery.prototype, "mockData", {
            get: function () {
                return this._mockData;
            },
            set: function (value) {
                this._mockData = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectQuery.prototype, "term", {
            get: function () {
                return this._term;
            },
            set: function (value) {
                this._term = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectQuery.prototype, "filter", {
            get: function () {
                return this._filter;
            },
            set: function (value) {
                this._filter = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectQuery.prototype, "dcpUrl", {
            get: function () {
                return this._dcpUrl;
            },
            set: function (value) {
                this._dcpUrl = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectQuery.prototype, "data", {
            get: function () {
                return this._data;
            },
            set: function (value) {
                this._data = value;
            },
            enumerable: true,
            configurable: true
        });
        RHDPProjectQuery.prototype.connectedCallback = function () {
            top.addEventListener('project-filter-change', this._filterChange);
            this.doSearch();
        };
        RHDPProjectQuery.prototype.doSearch = function () {
            var _this = this;
            var qUrl = new URL(this.dcpUrl);
            qUrl.searchParams.set('sort', 'sys_title');
            qUrl.searchParams.set('query', this.term);
            if (this.filter) {
                var upstreamProjects = this.productData[this.filter]['upstream'];
                for (var i = 0; i < upstreamProjects.length; i++) {
                    qUrl.searchParams.append('project', upstreamProjects[i]);
                }
            }
            if (!this.mockData) {
                fetch(qUrl.toString())
                    .then(function (resp) { return resp.json(); })
                    .then(function (data) {
                    _this.data = data;
                    _this.dispatchEvent(new CustomEvent('data-results-complete', {
                        detail: {
                            data: _this.data,
                            term: _this.term,
                            filter: _this.filter
                        },
                        bubbles: true
                    }));
                });
            }
        };
        RHDPProjectQuery.prototype._filterChange = function (e) {
            if (e.detail) {
                this.filter = e.detail.filter ? e.detail.filter : '';
                this.term = e.detail.term ? e.detail.term : '';
            }
            this.doSearch();
        };
        Object.defineProperty(RHDPProjectQuery, "observedAttributes", {
            get: function () {
                return ['loading', 'filter'];
            },
            enumerable: true,
            configurable: true
        });
        RHDPProjectQuery.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
        };
        return RHDPProjectQuery;
    }(HTMLElement));
    window.customElements.define('rhdp-project-query', RHDPProjectQuery);

    var __extends$3 = (undefined && undefined.__extends) || (function () {
        var extendStatics = function (d, b) {
            extendStatics = Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
                function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
            return extendStatics(d, b);
        };
        return function (d, b) {
            extendStatics(d, b);
            function __() { this.constructor = d; }
            d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
        };
    })();
    var RHDPProjectURL = (function (_super) {
        __extends$3(RHDPProjectURL, _super);
        function RHDPProjectURL() {
            var _this = _super.call(this) || this;
            _this._uri = new URL(window.location.href);
            _this._updateURI = _this._updateURI.bind(_this);
            return _this;
        }
        Object.defineProperty(RHDPProjectURL.prototype, "uri", {
            get: function () {
                return this._uri;
            },
            set: function (value) {
                this._uri = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectURL.prototype, "term", {
            get: function () {
                return this._term;
            },
            set: function (value) {
                if (value.length > 0) {
                    this.uri.searchParams.set('filter-text', value);
                }
                else {
                    this.uri.searchParams.delete('filter-text');
                }
                this._term = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjectURL.prototype, "filters", {
            get: function () {
                return this._filters;
            },
            set: function (value) {
                if (value.length > 0) {
                    this.uri.searchParams.set('filter-product', value);
                }
                else {
                    this.uri.searchParams.delete('filter-product');
                }
                this._filters = value;
            },
            enumerable: true,
            configurable: true
        });
        RHDPProjectURL.prototype.connectedCallback = function () {
            top.addEventListener('data-results-complete', this._updateURI);
        };
        RHDPProjectURL.prototype._updateURI = function (e) {
            if (e.detail) {
                this.term = e.detail.term ? e.detail.term : '';
                this.filters = e.detail.filter ? e.detail.filter : '';
                history.pushState({}, 'RHDP Projects:', "" + this.uri.pathname + (this.uri.searchParams ? "#!" + this.uri.searchParams : ''));
            }
        };
        Object.defineProperty(RHDPProjectURL, "observedAttributes", {
            get: function () {
                return ['loading'];
            },
            enumerable: true,
            configurable: true
        });
        RHDPProjectURL.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
        };
        return RHDPProjectURL;
    }(HTMLElement));
    window.customElements.define('rhdp-project-url', RHDPProjectURL);

    var __extends$4 = (undefined && undefined.__extends) || (function () {
        var extendStatics = function (d, b) {
            extendStatics = Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
                function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
            return extendStatics(d, b);
        };
        return function (d, b) {
            extendStatics(d, b);
            function __() { this.constructor = d; }
            d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
        };
    })();
    var __makeTemplateObject$2 = (undefined && undefined.__makeTemplateObject) || function (cooked, raw) {
        if (Object.defineProperty) { Object.defineProperty(cooked, "raw", { value: raw }); } else { cooked.raw = raw; }
        return cooked;
    };
    var RHDPProjects = (function (_super) {
        __extends$4(RHDPProjects, _super);
        function RHDPProjects() {
            var _this = _super.call(this) || this;
            _this._loading = true;
            _this._dcpUrl = '';
            _this._productId = '';
            _this.template = function (strings, project) {
                return "<ul class=\"small-block-grid-2 large-block-grid-4 medium-block-grid-3 results\"></ul>";
            };
            return _this;
        }
        Object.defineProperty(RHDPProjects.prototype, "dcpUrl", {
            get: function () {
                return this._dcpUrl;
            },
            set: function (value) {
                if (this._dcpUrl === value)
                    return;
                this._dcpUrl = value;
                this.setAttribute('dcp-url', this._dcpUrl);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjects.prototype, "productId", {
            get: function () {
                return this._productId;
            },
            set: function (value) {
                if (this._productId === value)
                    return;
                this._productId = value;
                this.setAttribute('upstream-product-id', this._productId);
                if (this.querySelector('rhdp-project-query')) {
                    this.querySelector('rhdp-project-query').setAttribute('filter', this._productId);
                }
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjects.prototype, "loading", {
            get: function () {
                return this._loading;
            },
            set: function (value) {
                if (value == false) {
                    this.querySelector('ul.results').classList.remove('loading');
                }
                else {
                    this.querySelector('ul.results').classList.add('loading');
                }
                this._loading = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPProjects.prototype, "data", {
            get: function () {
                return this._data;
            },
            set: function (value) {
                this._data = value;
            },
            enumerable: true,
            configurable: true
        });
        RHDPProjects.prototype.connectedCallback = function () {
            var query = new RHDPProjectQuery();
            var url = new RHDPProjectURL();
            this.innerHTML = this.template(templateObject_1$2 || (templateObject_1$2 = __makeTemplateObject$2(["", ""], ["", ""])), this);
            this.appendChild(query);
            this.appendChild(url);
            this.addEventListener('data-results-complete', this._loadDataResult);
            query.dcpUrl = this.dcpUrl;
            if (this.productId) {
                query.filter = this.productId;
            }
        };
        RHDPProjects.prototype.removeAllProjects = function () {
            var childNodes = this.querySelector('ul.results');
            while (childNodes.firstChild) {
                childNodes.removeChild(childNodes.firstChild);
            }
        };
        RHDPProjects.prototype._loadDataResult = function (e) {
            this.removeAllProjects();
            this.loading = true;
            if (e.detail && e.detail.data) {
                var hits = void 0;
                if (e.detail.data.responses) {
                    hits = e.detail.data.responses[0].hits.hits;
                }
                else {
                    hits = e.detail.data.hits.hits;
                }
                for (var i = 0; i < hits.length; i++) {
                    var project = new RHDPProjectItem();
                    var props = hits[i].fields;
                    var thumbnailSize = "200x150";
                    project.imageUrl = "https://static.jboss.org/" + (props.specialIcon || props.sys_project) + "/images/" + (props.specialIcon || props.sys_project) + "_" + thumbnailSize + ".png";
                    project.downloadsLink = props.downloadsLink;
                    project.projectName = props.sys_project_name;
                    project.sys_url_view = props.sys_url_view;
                    project.descriptions = props.description;
                    project.docsLink = props.docsLink;
                    project.communityLink = props.communityLink;
                    project.knowledgebaseLink = props.knowledgeBaseLink;
                    project.userForumLink = props.userForumLink;
                    project.devForumLink = props.devForumLink;
                    project.mailingListLink = props.mailingListLink;
                    project.chatLink = props.chatLink;
                    project.blogLink = props.blogLink;
                    project.issueTracker = props.issueTrackerLink;
                    project.jiraLink = props.jiraLink;
                    project.srcLink = props.srcLink;
                    project.anonymousLink = props.anonymousLink;
                    project.commiterLink = props.commiterLink;
                    project.fisheyeLink = props.fisheyeLink;
                    project.viewvcLink = props.viewvcLink;
                    project.githubLink = props.githubLink;
                    project.committerGitLink = props.committerGitLink;
                    project.buildLink = props.buildLink;
                    project.hudsonLink = props.hudsonLink;
                    var listItem = document.createElement('li');
                    listItem.setAttribute('class', 'upstream');
                    listItem.appendChild(project);
                    this.querySelector('ul.results').appendChild(listItem);
                }
                this.loading = false;
            }
        };
        Object.defineProperty(RHDPProjects, "observedAttributes", {
            get: function () {
                return ['dcp-url', 'upstream-product-id'];
            },
            enumerable: true,
            configurable: true
        });
        RHDPProjects.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            switch (name) {
                case 'dcp-url':
                    this.dcpUrl = newVal;
                    break;
                case 'upstream-product-id':
                    this.productId = newVal;
                    break;
                default:
                    this[name] = newVal;
            }
            this.innerHTML = this.template(templateObject_2$1 || (templateObject_2$1 = __makeTemplateObject$2(["", ""], ["", ""])), this);
        };
        return RHDPProjects;
    }(HTMLElement));
    window.customElements.define('rhdp-projects', RHDPProjects);
    var templateObject_1$2, templateObject_2$1;

    new RHDPProjects();
    new RHDPProjectQuery();
    new RHDPProjectURL();
    new RHDPProjectFilterBox();
    new RHDPProjectItem();

})));
