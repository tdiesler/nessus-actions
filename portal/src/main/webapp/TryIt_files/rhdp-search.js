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
    var RHDPSearchURL = (function (_super) {
        __extends(RHDPSearchURL, _super);
        function RHDPSearchURL() {
            var _this = _super.call(this) || this;
            _this._uri = new URL(window.location.href);
            _this._term = _this.uri.searchParams.get('t');
            _this._filters = _this._setFilters(_this.uri.searchParams.getAll('f'));
            _this._sort = _this.uri.searchParams.get('s') || 'relevance';
            _this._qty = _this.uri.searchParams.get('r');
            _this._init = true;
            _this._changeAttr = _this._changeAttr.bind(_this);
            _this._popState = _this._popState.bind(_this);
            return _this;
        }
        Object.defineProperty(RHDPSearchURL.prototype, "uri", {
            get: function () {
                return this._uri;
            },
            set: function (val) {
                if (this._uri === val)
                    return;
                this._uri = val;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchURL.prototype, "term", {
            get: function () {
                var ua = window.navigator.userAgent;
                var trident = ua.indexOf('Trident/');
                var isIE = trident > 0;
                var tmpTerm = this._term;
                if (isIE) {
                    tmpTerm = tmpTerm.replace("+", " ");
                }
                return tmpTerm;
            },
            set: function (val) {
                if (this._term === val)
                    return;
                this._term = val;
                this.uri.searchParams.set('t', this._term);
                this.setAttribute('term', this.term);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchURL.prototype, "filters", {
            get: function () {
                return this._filters;
            },
            set: function (val) {
                var _this = this;
                this._filters = val;
                this.uri.searchParams.delete('f');
                Object.keys(this._filters).forEach(function (group) {
                    _this.uri.searchParams.append('f', group + "~" + _this._filters[group].join(' '));
                });
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchURL.prototype, "sort", {
            get: function () {
                return this._sort;
            },
            set: function (val) {
                if (this._sort === val)
                    return;
                this._sort = val;
                this.uri.searchParams.set('s', this._sort);
                this.setAttribute('sort', this._sort);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchURL.prototype, "qty", {
            get: function () {
                return this._qty;
            },
            set: function (val) {
                if (this._qty === val)
                    return;
                this._qty = val;
                this.setAttribute('qty', this._sort);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchURL.prototype, "init", {
            get: function () {
                return this._init;
            },
            set: function (val) {
                if (this._init === val)
                    return;
                this._init = val;
            },
            enumerable: true,
            configurable: true
        });
        RHDPSearchURL.prototype.connectedCallback = function () {
            top.addEventListener('search-complete', this._changeAttr);
            top.addEventListener('clear-filters', this._changeAttr);
            top.window.addEventListener('popstate', this._popState);
            this._paramsReady();
        };
        Object.defineProperty(RHDPSearchURL, "observedAttributes", {
            get: function () {
                return ['sort', 'term', 'qty'];
            },
            enumerable: true,
            configurable: true
        });
        RHDPSearchURL.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
        };
        RHDPSearchURL.prototype._popState = function (e) {
            this.uri = new URL(document.location.href);
            this.term = this.uri.searchParams.get('t') || null;
            this.filters = this._setFilters(this.uri.searchParams.getAll('f'));
            this.sort = this.uri.searchParams.get('s');
            this.qty = this.uri.searchParams.get('r');
            this._paramsReady();
        };
        RHDPSearchURL.prototype._paramsReady = function () {
            this.dispatchEvent(new CustomEvent('params-ready', {
                detail: {
                    term: this.term,
                    filters: this.filters,
                    sort: this.sort,
                    qty: this.qty
                },
                bubbles: true
            }));
        };
        RHDPSearchURL.prototype._setFilters = function (filtersQS) {
            var filters = {};
            filtersQS.forEach(function (filter) {
                var kv = filter.split('~'), k = kv[0], v = kv[1].split(' ');
                filters[k] = v;
            });
            return filters;
        };
        RHDPSearchURL.prototype._changeAttr = function (e) {
            switch (e.type) {
                case 'clear-filters':
                    this.uri.searchParams.delete('f');
                    this.filters = {};
                    break;
                case 'load-more':
                    break;
                case 'search-complete':
                    if (e.detail && typeof e.detail.term !== 'undefined' && e.detail.term.length > 0) {
                        this.term = e.detail.term;
                    }
                    else {
                        this.term = '';
                        this.uri.searchParams.delete('t');
                    }
                    if (e.detail && e.detail.filters) {
                        this.filters = e.detail.filters;
                    }
                    if (e.detail && typeof e.detail.sort !== 'undefined') {
                        this.sort = e.detail.sort;
                    }
            }
            if (e.detail && typeof e.detail.invalid === 'undefined') {
                history.pushState({}, "RHDP Search: " + (this.term ? this.term : ''), "" + this.uri.pathname + this.uri.search);
            }
            else {
                this.term = '';
                this.filters = {};
                this.sort = 'relevance';
                this.uri.searchParams.delete('t');
                this.uri.searchParams.delete('f');
                this.uri.searchParams.delete('s');
                history.replaceState({}, 'RHDP Search Error', "" + this.uri.pathname + this.uri.search);
            }
        };
        return RHDPSearchURL;
    }(HTMLElement));
    customElements.define('rhdp-search-url', RHDPSearchURL);

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
    var RHDPSearchQuery = (function (_super) {
        __extends$1(RHDPSearchQuery, _super);
        function RHDPSearchQuery() {
            var _this = _super.call(this) || this;
            _this._limit = 10;
            _this._from = 0;
            _this._sort = 'relevance';
            _this._valid = true;
            _this.urlTemplate = function (strings, url, term, from, limit, sort, types, tags, sys_types) {
                var order = '';
                if (sort === 'most-recent') {
                    order = '&newFirst=true';
                }
                return url + "?tags_or_logic=true&filter_out_excluded=true&from=" + from + order + "&query=" + term + "&query_highlight=true&size" + limit + "=true" + types + tags + sys_types;
            };
            _this._changeAttr = _this._changeAttr.bind(_this);
            return _this;
        }
        Object.defineProperty(RHDPSearchQuery.prototype, "filters", {
            get: function () {
                return this._filters;
            },
            set: function (val) {
                if (this._filters === val)
                    return;
                this._filters = val;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchQuery.prototype, "activeFilters", {
            get: function () {
                return this._activeFilters;
            },
            set: function (val) {
                if (this._activeFilters === val)
                    return;
                this._activeFilters = val;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchQuery.prototype, "from", {
            get: function () {
                return this._from;
            },
            set: function (val) {
                if (this._from === val)
                    return;
                this._from = val;
                this.setAttribute('from', val.toString());
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchQuery.prototype, "limit", {
            get: function () {
                return this._limit;
            },
            set: function (val) {
                if (this._limit === val)
                    return;
                this._limit = val;
                this.setAttribute('limit', val.toString());
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchQuery.prototype, "sort", {
            get: function () {
                return this._sort;
            },
            set: function (val) {
                if (this._sort === val)
                    return;
                this._sort = val;
                this.setAttribute('sort', val);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchQuery.prototype, "results", {
            get: function () {
                return this._results;
            },
            set: function (val) {
                if (this._results === val)
                    return;
                this._results = val;
                this.from = this.results && this.results.hits && typeof this.results.hits.hits !== 'undefined' ? this.from + this.results.hits.hits.length : 0;
                this.dispatchEvent(new CustomEvent('search-complete', {
                    detail: {
                        term: this.term,
                        filters: this.activeFilters,
                        sort: this.sort,
                        limit: this.limit,
                        from: this.from,
                        results: this.results,
                    },
                    bubbles: true
                }));
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchQuery.prototype, "term", {
            get: function () {
                return this._term;
            },
            set: function (val) {
                if (this._term === val)
                    return;
                this._term = val;
                this.setAttribute('term', val.toString());
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchQuery.prototype, "url", {
            get: function () {
                return this._url;
            },
            set: function (val) {
                if (this._url === val)
                    return;
                this._url = val;
                this.setAttribute('url', val.toString());
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchQuery.prototype, "valid", {
            get: function () {
                return this._valid;
            },
            set: function (val) {
                if (this._valid === val)
                    return;
                this._valid = val;
            },
            enumerable: true,
            configurable: true
        });
        RHDPSearchQuery.prototype.filterString = function (facets) {
            var len = facets.length, filterArr = [];
            for (var i = 0; i < len; i++) {
                for (var j = 0; j < facets[i].items.length; j++) {
                    if (facets[i].items[j].active) {
                        var idx = 0;
                        while (idx < facets[i].items[j].value.length) {
                            filterArr.push(facets[i].items[j].value[idx]);
                            idx = idx + 1;
                        }
                    }
                }
            }
            return filterArr.join(', ');
        };
        RHDPSearchQuery.prototype.connectedCallback = function () {
            top.addEventListener('params-ready', this._changeAttr);
            top.addEventListener('term-change', this._changeAttr);
            top.addEventListener('filter-item-change', this._changeAttr);
            top.addEventListener('sort-change', this._changeAttr);
            top.addEventListener('clear-filters', this._changeAttr);
            top.addEventListener('load-more', this._changeAttr);
        };
        Object.defineProperty(RHDPSearchQuery, "observedAttributes", {
            get: function () {
                return ['term', 'sort', 'limit', 'results', 'url'];
            },
            enumerable: true,
            configurable: true
        });
        RHDPSearchQuery.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
        };
        RHDPSearchQuery.prototype._setFilters = function (item) {
            var _this = this;
            var add = item.active;
            if (add) {
                this.activeFilters[item.group] = this.activeFilters[item.group] || [];
                this.activeFilters[item.group].push(item.key);
            }
            else {
                Object.keys(this.activeFilters).forEach(function (group) {
                    if (group === item.group) {
                        var idx = _this.activeFilters[group].indexOf(item.key);
                        if (idx >= 0) {
                            _this.activeFilters[group].splice(idx, 1);
                            if (_this.activeFilters[group].length === 0) {
                                delete _this.activeFilters[group];
                            }
                        }
                    }
                });
            }
        };
        RHDPSearchQuery.prototype._changeAttr = function (e) {
            switch (e.type) {
                case 'term-change':
                    if (e.detail && e.detail.term && e.detail.term.length > 0) {
                        this.term = e.detail.term;
                    }
                    else {
                        this.term = '';
                    }
                    this.from = 0;
                    this.search();
                    break;
                case 'filter-item-change':
                    if (e.detail && e.detail.facet) {
                        this._setFilters(e.detail.facet);
                    }
                    this.from = 0;
                    this.search();
                    break;
                case 'sort-change':
                    if (e.detail && e.detail.sort) {
                        this.sort = e.detail.sort;
                    }
                    this.from = 0;
                    this.search();
                    break;
                case 'load-more':
                    this.search();
                    break;
                case 'clear-filters':
                    this.activeFilters = {};
                    this.search();
                    break;
                case 'params-ready':
                    if (e.detail && e.detail.term) {
                        this.term = e.detail.term;
                    }
                    if (e.detail && e.detail.sort) {
                        this.sort = e.detail.sort;
                    }
                    if (e.detail && e.detail.filters) {
                        this.activeFilters = e.detail.filters;
                    }
                    this.from = 0;
                    if (Object.keys(e.detail.filters).length > 0 || e.detail.term !== null || e.detail.sort !== null || e.detail.qty !== null) {
                        this.search();
                    }
                    break;
            }
        };
        RHDPSearchQuery.prototype.search = function () {
            var _this = this;
            this.dispatchEvent(new CustomEvent('search-start', { bubbles: true }));
            if (Object.keys(this.activeFilters).length > 0 || (this.term !== null && this.term !== '' && typeof this.term !== 'undefined')) {
                var qURL_1 = new URL(this.url);
                qURL_1.searchParams.set('tags_or_logic', 'true');
                qURL_1.searchParams.set('filter_out_excluded', 'true');
                qURL_1.searchParams.set('from', this.from.toString());
                if (this.sort === 'most-recent') {
                    qURL_1.searchParams.set('newFirst', 'true');
                }
                qURL_1.searchParams.set('query', this.term || '');
                qURL_1.searchParams.set('query_highlight', 'true');
                qURL_1.searchParams.set('size' + this.limit.toString(), 'true');
                if (this.activeFilters) {
                    Object.keys(this.activeFilters).forEach(function (filtergroup) {
                        _this.filters.facets.forEach(function (group) {
                            if (group.key === filtergroup) {
                                group.items.forEach(function (facet) {
                                    if (_this.activeFilters[group.key].indexOf(facet.key) >= 0) {
                                        facet.value.forEach(function (fval) {
                                            qURL_1.searchParams.append(group.key, fval);
                                        });
                                    }
                                });
                            }
                        });
                    });
                }
                fetch(qURL_1.toString())
                    .then(function (resp) { return resp.json(); })
                    .then(function (data) {
                    _this.results = data;
                });
            }
            else {
                this.dispatchEvent(new CustomEvent('search-complete', { detail: { invalid: true }, bubbles: true }));
            }
        };
        return RHDPSearchQuery;
    }(HTMLElement));
    customElements.define('rhdp-search-query', RHDPSearchQuery);

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
    var __makeTemplateObject = (undefined && undefined.__makeTemplateObject) || function (cooked, raw) {
        if (Object.defineProperty) { Object.defineProperty(cooked, "raw", { value: raw }); } else { cooked.raw = raw; }
        return cooked;
    };
    var RHDPSearchBox = (function (_super) {
        __extends$2(RHDPSearchBox, _super);
        function RHDPSearchBox() {
            var _this = _super.call(this) || this;
            _this._term = '';
            _this.name = 'Search Box';
            _this.template = function (strings, name, term) {
                return "\n        <form class=\"search-bar\" role=\"search\">\n            <div class=\"pf-c-input-group\" role=\"search\">\n              <input class=\"pf-c-form-control\" type=\"search\" id=\"query\" aria-label=\"Search input\" value=\"" + term + "\" placeholder=\"Enter your search term\" />\n              <button class=\"pf-c-button pf-m-control pf-m-danger\" type=\"button\" aria-label=\"button for search input\" data-search-action=\"searchSubmit\">\n                <i class=\"fas fa-search\" aria-hidden=\"true\"></i>\n              </button>\n            </div>\n        </form>";
            };
            _this._checkTerm = _this._checkTerm.bind(_this);
            return _this;
        }
        Object.defineProperty(RHDPSearchBox.prototype, "term", {
            get: function () {
                return this._term;
            },
            set: function (val) {
                if (this._term === val)
                    return;
                this._term = decodeURI(val);
                this.querySelector('input').setAttribute('value', this.term);
            },
            enumerable: true,
            configurable: true
        });
        RHDPSearchBox.prototype.connectedCallback = function () {
            var _this = this;
            top.addEventListener('params-ready', this._checkTerm);
            top.addEventListener('term-change', this._checkTerm);
            this.innerHTML = this.template(templateObject_1 || (templateObject_1 = __makeTemplateObject(["", "", ""], ["", "", ""])), this.name, this.term);
            this.addEventListener('submit', function (e) {
                e.preventDefault();
                _this._termChange();
                return false;
            });
            this.querySelector('button[data-search-action="searchSubmit"]').addEventListener('click', function (e) {
                _this._termChange();
            });
        };
        Object.defineProperty(RHDPSearchBox, "observedAttributes", {
            get: function () {
                return ['term'];
            },
            enumerable: true,
            configurable: true
        });
        RHDPSearchBox.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
        };
        RHDPSearchBox.prototype._checkTerm = function (e) {
            if (e.detail && e.detail.term) {
                this.term = e.detail.term;
            }
        };
        RHDPSearchBox.prototype._termChange = function () {
            this.term = this.querySelector('input').value;
            this.dispatchEvent(new CustomEvent('term-change', {
                detail: {
                    term: this.term
                },
                bubbles: true
            }));
        };
        return RHDPSearchBox;
    }(HTMLElement));
    customElements.define('rhdp-search-box', RHDPSearchBox);
    var templateObject_1;

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
    var RHDPSearchResultCount = (function (_super) {
        __extends$3(RHDPSearchResultCount, _super);
        function RHDPSearchResultCount() {
            var _this = _super.call(this) || this;
            _this._count = 0;
            _this._term = '';
            _this._loading = true;
            _this.template = function (strings, count, term) {
                return count + " results found for " + term.replace('<', '&lt;').replace('>', '&gt;');
            };
            _this._setText = _this._setText.bind(_this);
            return _this;
        }
        Object.defineProperty(RHDPSearchResultCount.prototype, "count", {
            get: function () {
                return this._count;
            },
            set: function (val) {
                if (this._count === val)
                    return;
                this._count = val;
                this.setAttribute('count', val.toString());
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchResultCount.prototype, "term", {
            get: function () {
                return this._term;
            },
            set: function (val) {
                val = decodeURI(val).replace('<', '&lt;').replace('>', '&gt;');
                if (this._term === val)
                    return;
                this._term = val;
                this.setAttribute('term', val);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchResultCount.prototype, "loading", {
            get: function () {
                return this._loading;
            },
            set: function (val) {
                if (this._loading === val)
                    return;
                this._loading = val;
            },
            enumerable: true,
            configurable: true
        });
        RHDPSearchResultCount.prototype.connectedCallback = function () {
            var _this = this;
            top.addEventListener('params-ready', this._setText);
            top.addEventListener('search-start', function (e) { _this.loading = true; _this._setText(e); });
            top.addEventListener('search-complete', function (e) { _this.loading = false; _this._setText(e); });
        };
        Object.defineProperty(RHDPSearchResultCount, "observedAttributes", {
            get: function () {
                return ['count', 'term'];
            },
            enumerable: true,
            configurable: true
        });
        RHDPSearchResultCount.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
            this.innerHTML = this.count + " results found " + (this.term ? "for " + this.term : '');
        };
        RHDPSearchResultCount.prototype._setText = function (e) {
            if (e.detail) {
                if (typeof e.detail.invalid === 'undefined') {
                    if (e.detail.term && e.detail.term.length > 0) {
                        this.term = e.detail.term;
                    }
                    else {
                        this.term = '';
                    }
                    if (e.detail.results && e.detail.results.hits && e.detail.results.hits.total) {
                        this.count = e.detail.results.hits.total;
                    }
                    else {
                        this.count = 0;
                    }
                    if (!this.loading) {
                        this.innerHTML = this.count + " results found " + (this.term ? "for " + this.term : '');
                    }
                }
                else {
                    this.term = '';
                    this.count = 0;
                    this.innerHTML = '';
                }
            }
            else {
                this.term = '';
                this.count = 0;
                this.innerHTML = '';
            }
        };
        return RHDPSearchResultCount;
    }(HTMLElement));
    customElements.define('rhdp-search-result-count', RHDPSearchResultCount);

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
    var __makeTemplateObject$1 = (undefined && undefined.__makeTemplateObject) || function (cooked, raw) {
        if (Object.defineProperty) { Object.defineProperty(cooked, "raw", { value: raw }); } else { cooked.raw = raw; }
        return cooked;
    };
    var RHDPSearchFilterGroup = (function (_super) {
        __extends$4(RHDPSearchFilterGroup, _super);
        function RHDPSearchFilterGroup() {
            var _this = _super.call(this) || this;
            _this._toggle = false;
            _this._more = false;
            _this.template = function (strings, name) {
                return "\n        <div class=\"showFilters heading\">\n          <span class=\"group-name\">" + name + "</span>\n          <span class=\"toggle\"><i class=\"fa fa-chevron-right\" aria-hidden=\"true\"></i></span>\n        </div>\n        <div class=\"group hide\">\n            <div class=\"primary\"></div>\n            <div class=\"secondary hide\"></div>\n            <a href=\"#\" class=\"more\" data-search-action=\"more\">Show More</a>\n        </div>";
            };
            _this.innerHTML = _this.template(templateObject_1$1 || (templateObject_1$1 = __makeTemplateObject$1(["", ""], ["", ""])), _this.name);
            return _this;
        }
        Object.defineProperty(RHDPSearchFilterGroup.prototype, "key", {
            get: function () {
                return this._key;
            },
            set: function (val) {
                if (this._key === val)
                    return;
                this._key = val;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchFilterGroup.prototype, "name", {
            get: function () {
                return this._name;
            },
            set: function (val) {
                if (this._name === val)
                    return;
                this._name = val;
                this.querySelector('.group-name').innerHTML = this._name;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchFilterGroup.prototype, "items", {
            get: function () {
                return this._items;
            },
            set: function (val) {
                if (this._items === val)
                    return;
                this._items = val;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchFilterGroup.prototype, "toggle", {
            get: function () {
                return this._toggle;
            },
            set: function (val) {
                if (this._toggle === val)
                    return;
                this._toggle = val;
                this.querySelector('.group').className = this.toggle ? 'group' : 'group hide';
                this.querySelector('.toggle').className = this.toggle ? 'toggle expand' : 'toggle';
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchFilterGroup.prototype, "more", {
            get: function () {
                return this._more;
            },
            set: function (val) {
                if (this._more === val)
                    return;
                this._more = val;
                this.querySelector('.more').innerHTML = this.more ? 'Show Less' : 'Show More';
                this.querySelector('.secondary').className = this.more ? 'secondary' : 'secondary hide';
            },
            enumerable: true,
            configurable: true
        });
        RHDPSearchFilterGroup.prototype.connectedCallback = function () {
            var _this = this;
            this.querySelector('.heading').addEventListener('click', function (e) {
                e.preventDefault();
                _this.toggle = !_this.toggle;
            });
            this.querySelector('.more').addEventListener('click', function (e) {
                _this.more = !_this.more;
            });
            this.toggle = true;
        };
        Object.defineProperty(RHDPSearchFilterGroup, "observedAttributes", {
            get: function () {
                return ['name', 'key', 'toggle', 'items', 'more'];
            },
            enumerable: true,
            configurable: true
        });
        RHDPSearchFilterGroup.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
        };
        return RHDPSearchFilterGroup;
    }(HTMLElement));
    customElements.define('rhdp-search-filter-group', RHDPSearchFilterGroup);
    var templateObject_1$1;

    var __extends$5 = (undefined && undefined.__extends) || (function () {
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
    var RHDPSearchFilterItem = (function (_super) {
        __extends$5(RHDPSearchFilterItem, _super);
        function RHDPSearchFilterItem() {
            var _this = _super.call(this) || this;
            _this._active = false;
            _this._inline = false;
            _this._bubble = true;
            _this._bounce = false;
            _this.template = function (strings, name, key, active) {
                return "\n        <div class=\"pf-c-check\">\n          <input class=\"pf-c-check__input\" type=\"checkbox\" id=\"filter-item-" + key + "\" name=\"filter-item-" + key + "\"/>\n          <label class=\"pf-c-check__label\" for=\"filter-item-" + key + "\">" + name + "</label>\n        </div>\n         ";
            };
            _this.inlineTemplate = function (strings, name, active) {
                return active ? "\n        <div class=\"pf-c-label\" data-search-action=\"clearFilter\">\n            " + name + " <i class=\"fa fa-times\" aria-hidden=\"true\" data-search-action=\"clearFilter\"></i>\n        </div>" : '';
            };
            _this._checkParams = _this._checkParams.bind(_this);
            _this._clearFilters = _this._clearFilters.bind(_this);
            _this._checkChange = _this._checkChange.bind(_this);
            _this._updateFacet = _this._updateFacet.bind(_this);
            return _this;
        }
        Object.defineProperty(RHDPSearchFilterItem.prototype, "name", {
            get: function () {
                return this._name;
            },
            set: function (val) {
                if (this._name === val)
                    return;
                this._name = val;
                this.setAttribute('name', this._name);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchFilterItem.prototype, "key", {
            get: function () {
                return this._key;
            },
            set: function (val) {
                if (this._key === val)
                    return;
                this._key = val;
                this.className = "filter-item-" + this._key;
                this.setAttribute('key', this._key);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchFilterItem.prototype, "group", {
            get: function () {
                return this._group;
            },
            set: function (val) {
                if (this._group === val)
                    return;
                this._group = val;
                this.setAttribute('group', this._group);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchFilterItem.prototype, "active", {
            get: function () {
                return this._active;
            },
            set: function (val) {
                if (typeof val === 'string') {
                    val = true;
                }
                if (val === null) {
                    val = false;
                }
                if (this._active === val) {
                    return;
                }
                this._active = val;
                var chkbox = this.querySelector('input');
                if (this._active) {
                    this.setAttribute('active', '');
                }
                else {
                    this.removeAttribute('active');
                }
                if (chkbox) {
                    chkbox.checked = this._active;
                }
                if (this.inline) {
                    this.innerHTML = this._active ? this.inlineTemplate(templateObject_1$2 || (templateObject_1$2 = __makeTemplateObject$2(["", "", ""], ["", "", ""])), this.name, this._active) : '';
                }
                this.dispatchEvent(new CustomEvent('filter-item-change', { detail: { facet: this }, bubbles: this.bubble }));
                this.bubble = true;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchFilterItem.prototype, "value", {
            get: function () {
                return this._value;
            },
            set: function (val) {
                if (this._value === val)
                    return;
                this._value = val;
                this.setAttribute('value', this.value);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchFilterItem.prototype, "inline", {
            get: function () {
                return this._inline;
            },
            set: function (val) {
                if (this._inline === val)
                    return;
                this._inline = val;
                this.innerHTML = !this._inline ? this.template(templateObject_2 || (templateObject_2 = __makeTemplateObject$2(["", "", "", ""], ["", "", "", ""])), this.name, this.key, this.active) : this.inlineTemplate(templateObject_3 || (templateObject_3 = __makeTemplateObject$2(["", "", ""], ["", "", ""])), this.name, this.active);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchFilterItem.prototype, "bubble", {
            get: function () {
                return this._bubble;
            },
            set: function (val) {
                if (this._bubble === val)
                    return;
                this._bubble = val;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchFilterItem.prototype, "bounce", {
            get: function () {
                return this._bounce;
            },
            set: function (val) {
                if (this._bounce === val)
                    return;
                this._bounce = val;
            },
            enumerable: true,
            configurable: true
        });
        RHDPSearchFilterItem.prototype.connectedCallback = function () {
            this.innerHTML = !this.inline ? this.template(templateObject_4 || (templateObject_4 = __makeTemplateObject$2(["", "", "", ""], ["", "", "", ""])), this.name, this.key, this.active) : this.inlineTemplate(templateObject_5 || (templateObject_5 = __makeTemplateObject$2(["", "", ""], ["", "", ""])), this.name, this.active);
            if (!this.inline) {
                this.addEventListener('change', this._updateFacet);
            }
            else {
                this.addEventListener('click', this._updateFacet);
            }
            top.addEventListener('filter-item-change', this._checkChange);
            top.addEventListener('params-ready', this._checkParams);
            top.addEventListener('clear-filters', this._clearFilters);
        };
        Object.defineProperty(RHDPSearchFilterItem, "observedAttributes", {
            get: function () {
                return ['name', 'active', 'value', 'inline', 'key', 'group'];
            },
            enumerable: true,
            configurable: true
        });
        RHDPSearchFilterItem.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
        };
        RHDPSearchFilterItem.prototype._updateFacet = function (e) {
            this.bounce = true;
            if (this.inline) {
                var action = e.target['dataset']['searchAction'];
                if (action && action == 'clearFilter') {
                    this.active = !this.active;
                }
            }
            else {
                this.active = !this.active;
            }
        };
        RHDPSearchFilterItem.prototype._checkParams = function (e) {
            var _this = this;
            var chk = false;
            if (e.detail && e.detail.filters) {
                Object.keys(e.detail.filters).forEach(function (group) {
                    e.detail.filters[group].forEach(function (facet) {
                        if (group === _this.group) {
                            if (facet === _this.key) {
                                chk = true;
                                _this.bubble = false;
                                _this.active = true;
                                _this.dispatchEvent(new CustomEvent('filter-item-init', { detail: { facet: _this }, bubbles: _this.bubble }));
                            }
                        }
                    });
                });
            }
            if (!chk) {
                this.bubble = false;
                this.active = false;
            }
        };
        RHDPSearchFilterItem.prototype._checkChange = function (e) {
            if (e.detail && e.detail.facet) {
                if (!this.bounce) {
                    if (this.group === e.detail.facet.group && this.key === e.detail.facet.key) {
                        this.bubble = false;
                        this.active = e.detail.facet.active;
                    }
                }
                this.bubble = true;
                this.bounce = false;
            }
        };
        RHDPSearchFilterItem.prototype._clearFilters = function (e) {
            this.bubble = false;
            this.bounce = false;
            this.active = false;
        };
        return RHDPSearchFilterItem;
    }(HTMLElement));
    customElements.define('rhdp-search-filter-item', RHDPSearchFilterItem);
    var templateObject_1$2, templateObject_2, templateObject_3, templateObject_4, templateObject_5;

    var __extends$6 = (undefined && undefined.__extends) || (function () {
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
    var __makeTemplateObject$3 = (undefined && undefined.__makeTemplateObject) || function (cooked, raw) {
        if (Object.defineProperty) { Object.defineProperty(cooked, "raw", { value: raw }); } else { cooked.raw = raw; }
        return cooked;
    };
    var RHDPSearchFilters = (function (_super) {
        __extends$6(RHDPSearchFilters, _super);
        function RHDPSearchFilters() {
            var _this = _super.call(this) || this;
            _this._type = '';
            _this._title = 'Filter By';
            _this._toggle = false;
            _this.modalTemplate = function (string, title) {
                return "<div class=\"cover\" id=\"cover\">\n            <div class=\"title pf-l-flex\">\n              <div class=\"pf-l-flex__item\">" + title + "</div>\n              <div class=\"pf-l-flex__item pf-m-align-right\">\n                <a href=\"#\" id=\"cancel\" data-search-action=\"cancelFilters\" class=\"pf-c-button pf-m-link\">\n                   Close\n                </a>\n              </div>\n            </div>\n            <div class=\"groups\">\n            </div>\n            <div class=\"footer\">\n            <a href=\"#\" class=\"pf-c-button pf-m-link-on-dark\"\n              data-search-action=\"clearFilters\">Clear Filters</a>\n            <a href=\"#\" data-search-action=\"applyFilters\" class=\"pf-c-button pf-m-secondary-on-dark\">Apply</a>\n            </div>\n        </div>";
            };
            _this.activeTemplate = function (strings, title) {
                return "<div class=\"active-type\">\n        <a href=\"#\" class=\"pf-c-button pf-m-link pf-u-float-right\"\n          data-search-action=\"clearFilters\">Clear Filters</a>\n        <div class=\"activeFilters\"></div>\n      </div>";
            };
            _this.template = function (strings, title) {
                return "\n          <div class=\"mobile\">\n              <a class=\"pf-c-button pf-m-link\" href=\"#\" data-search-action=\"showFilters\">Show Filters</a>\n          </div>\n          <div class=\"control\" id=\"control\">\n            <div class=\"title\">" + title + "</div>\n            <div class=\"groups\">\n            </div>\n          </div>";
            };
            _this._toggleModal = _this._toggleModal.bind(_this);
            _this._clearFilters = _this._clearFilters.bind(_this);
            _this._addFilters = _this._addFilters.bind(_this);
            _this._checkActive = _this._checkActive.bind(_this);
            return _this;
        }
        Object.defineProperty(RHDPSearchFilters.prototype, "type", {
            get: function () {
                return this._type;
            },
            set: function (val) {
                if (this._type === val)
                    return;
                this._type = val;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchFilters.prototype, "title", {
            get: function () {
                return this._title;
            },
            set: function (val) {
                if (this._title === val)
                    return;
                this._title = val;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchFilters.prototype, "filters", {
            get: function () {
                return this._filters;
            },
            set: function (val) {
                if (this._filters === val)
                    return;
                this._filters = val;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchFilters.prototype, "toggle", {
            get: function () {
                return this._toggle;
            },
            set: function (val) {
                if (this._toggle === val)
                    return;
                this._toggle = val;
                if (this._toggle) {
                    this.querySelector('.cover').className = 'cover modal';
                    window.scrollTo(0, 0);
                    document.body.style.overflow = 'hidden';
                    this.style.height = window.innerHeight + 'px';
                }
                else {
                    this.querySelector('.cover').className = 'cover';
                    document.body.style.overflow = 'auto';
                }
            },
            enumerable: true,
            configurable: true
        });
        RHDPSearchFilters.prototype.connectedCallback = function () {
            var _this = this;
            if (this.type === 'active') {
                this.innerHTML = this.activeTemplate(templateObject_1$3 || (templateObject_1$3 = __makeTemplateObject$3(["", ""], ["", ""])), this.title);
                top.addEventListener('filter-item-change', this._checkActive);
                top.addEventListener('filter-item-init', this._checkActive);
                top.addEventListener('search-complete', this._checkActive);
                top.addEventListener('params-ready', this._checkActive);
                top.addEventListener('clear-filters', this._clearFilters);
                this._addFilters();
            }
            else if (this.type === 'modal') {
                this.innerHTML = this.modalTemplate(templateObject_2$1 || (templateObject_2$1 = __makeTemplateObject$3(["", ""], ["", ""])), this.title);
                this.addGroups();
            }
            else {
                this.innerHTML = this.template(templateObject_3$1 || (templateObject_3$1 = __makeTemplateObject$3(["", ""], ["", ""])), this.title);
                this.addGroups();
            }
            this.addEventListener('click', function (e) {
                switch (e.target['dataset']['searchAction']) {
                    case 'showFilters':
                    case 'cancelFilters':
                    case 'applyFilters':
                        e.preventDefault();
                        _this.dispatchEvent(new CustomEvent('toggle-modal', {
                            bubbles: true
                        }));
                        break;
                    case 'clearFilters':
                        e.preventDefault();
                        _this.dispatchEvent(new CustomEvent('clear-filters', {
                            bubbles: true
                        }));
                        break;
                    case 'more':
                        e.preventDefault();
                        break;
                }
            });
            top.addEventListener('toggle-modal', this._toggleModal);
        };
        Object.defineProperty(RHDPSearchFilters, "observedAttributes", {
            get: function () {
                return ['type', 'title', 'toggle'];
            },
            enumerable: true,
            configurable: true
        });
        RHDPSearchFilters.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
        };
        RHDPSearchFilters.prototype.addGroups = function () {
            var groups = this.filters.facets, len = groups.length;
            for (var i = 0; i < len; i++) {
                var group = new RHDPSearchFilterGroup(), groupInfo = groups[i], groupNode = group.querySelector('.group'), primaryFilters = group.querySelector('.primary'), secondaryFilters = group.querySelector('.secondary'), len_1 = groupInfo.items ? groupInfo.items.length : 0;
                if (len_1 <= 5) {
                    groupNode.removeChild(groupNode.lastChild);
                }
                for (var j = 0; j < len_1; j++) {
                    var item = new RHDPSearchFilterItem();
                    item.name = groupInfo.items[j].name;
                    item.value = groupInfo.items[j].value;
                    item.active = groupInfo.items[j].active;
                    item.key = groupInfo.items[j].key;
                    item.group = groupInfo.key;
                    if (j < 5) {
                        primaryFilters.appendChild(item);
                    }
                    else {
                        secondaryFilters.appendChild(item);
                    }
                }
                group.key = groupInfo.key;
                group.name = groupInfo.name;
                this.querySelector('.groups').appendChild(group);
            }
        };
        RHDPSearchFilters.prototype._checkActive = function (e) {
            if (e.detail) {
                if (e.detail.facet) {
                    this.style.display = e.detail.facet.active ? 'block' : this.style.display;
                }
                else {
                    var chk = this.querySelectorAll('rhdp-search-filter-item[active]');
                    if (chk.length > 0) {
                        this.style.display = 'block';
                    }
                    else {
                        this.style.display = 'none';
                    }
                }
            }
        };
        RHDPSearchFilters.prototype._initActive = function (e, group_key, item) {
            if (e.detail && e.detail.filters) {
                Object.keys(e.detail.filters).forEach(function (group) {
                    e.detail.filters[group].forEach(function (facet) {
                        if (group === group_key) {
                            if (facet === item.key) {
                                return true;
                            }
                        }
                    });
                });
            }
            return false;
        };
        RHDPSearchFilters.prototype._addFilters = function () {
            var groups = this.filters.facets;
            for (var i = 0; i < groups.length; i++) {
                var items = groups[i].items;
                for (var j = 0; j < items.length; j++) {
                    var item = new RHDPSearchFilterItem();
                    item.name = items[j].name;
                    item.value = items[j].value;
                    item.inline = true;
                    item.bubble = false;
                    item.key = items[j].key;
                    item.group = groups[i].key;
                    this.querySelector('.activeFilters').appendChild(item);
                }
            }
        };
        RHDPSearchFilters.prototype._toggleModal = function (e) {
            if (this.type === 'modal') {
                this.toggle = !this.toggle;
            }
        };
        RHDPSearchFilters.prototype.applyFilters = function () {
            this.dispatchEvent(new CustomEvent('apply-filters', {
                bubbles: true
            }));
        };
        RHDPSearchFilters.prototype._clearFilters = function (e) {
            this.style.display = 'none';
        };
        return RHDPSearchFilters;
    }(HTMLElement));
    customElements.define('rhdp-search-filters', RHDPSearchFilters);
    var templateObject_1$3, templateObject_2$1, templateObject_3$1;

    var __extends$7 = (undefined && undefined.__extends) || (function () {
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
    var __makeTemplateObject$4 = (undefined && undefined.__makeTemplateObject) || function (cooked, raw) {
        if (Object.defineProperty) { Object.defineProperty(cooked, "raw", { value: raw }); } else { cooked.raw = raw; }
        return cooked;
    };
    var RHDPSearchOneBox = (function (_super) {
        __extends$7(RHDPSearchOneBox, _super);
        function RHDPSearchOneBox() {
            var _this = _super.call(this) || this;
            _this._term = '';
            _this._url = '../rhdp-apps/onebox/onebox.json';
            _this._mock = false;
            _this.slotTemplate = function (strings, slot, id) {
                return "" + (slot && slot.url && slot.text ? "<div class=\"pf-l-flex__item\"><a class=\"pf-c-button\" href=\"" + slot.url + "?onebox=" + id + "\">" + _this.getIcon(slot.icon) + " &nbsp; " + slot.text + "</a></div>" : '');
            };
            _this.template = function (strings, feature) {
                return "<div>\n            " + (feature.heading && feature.heading.url && feature.heading.text ? "<h4><a href=\"" + feature.heading.url + "\">" + feature.heading.text + "</a></h4>" : '') + "\n            " + (feature.details ? "<p>" + feature.details + "</p>" : '') + "\n            <div class=\"pf-l-flex\">\n              " + (feature.button && feature.button.url && feature.button.text ? "\n                <div class=\"pf-l-flex__item\">\n                    <a href=\"" + feature.button.url + "?onebox=" + feature.id + "\" class=\"pf-c-button pf-m-primary\">" + feature.button.text + "</a>\n                </div>" : '') + "\n              " + (feature.slots && feature.slots.length > 0 ? "\n              " + feature.slots.map(function (slot) { return _this.slotTemplate(templateObject_1$4 || (templateObject_1$4 = __makeTemplateObject$4(["", "", ""], ["", "", ""])), slot, feature.id); }).join('') : '') + "\n            </div>\n        </div>";
            };
            _this._termChange = _this._termChange.bind(_this);
            return _this;
        }
        Object.defineProperty(RHDPSearchOneBox.prototype, "term", {
            get: function () {
                if ((this._term === null) || (this._term === '')) {
                    return this._term;
                }
                else {
                    return this._term.replace(/(<([^>]+)>)/ig, '');
                }
            },
            set: function (val) {
                if (this._term === val)
                    return;
                this._term = val;
                this.setAttribute('term', this._term);
                this.feature = this.getFeature();
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchOneBox.prototype, "url", {
            get: function () {
                return this._url;
            },
            set: function (val) {
                if (this._url === val)
                    return;
                this._url = val;
                this.setAttribute('url', this._url);
                this.getData();
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchOneBox.prototype, "data", {
            get: function () {
                return this._data;
            },
            set: function (val) {
                if (this._data === val)
                    return;
                this._data = val;
                this.feature = this.getFeature();
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchOneBox.prototype, "feature", {
            get: function () {
                return this._feature;
            },
            set: function (val) {
                if (this._feature === val)
                    return;
                this._feature = val;
                this.innerHTML = this.feature ? this.template(templateObject_2$2 || (templateObject_2$2 = __makeTemplateObject$4(["", ""], ["", ""])), this.feature) : '';
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchOneBox.prototype, "mock", {
            get: function () {
                return this._mock;
            },
            set: function (val) {
                if (this._mock === val)
                    return;
                this._mock = val;
            },
            enumerable: true,
            configurable: true
        });
        RHDPSearchOneBox.prototype.connectedCallback = function () {
            this.getData();
            top.addEventListener('term-change', this._termChange);
            top.addEventListener('params-ready', this._termChange);
        };
        Object.defineProperty(RHDPSearchOneBox, "observedAttributes", {
            get: function () {
                return ['term', 'url', 'mock'];
            },
            enumerable: true,
            configurable: true
        });
        RHDPSearchOneBox.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
        };
        RHDPSearchOneBox.prototype._termChange = function (e) {
            if (e.detail && e.detail.term && e.detail.term.length > 0) {
                this.term = e.detail.term;
            }
            else {
                this.term = '';
            }
        };
        RHDPSearchOneBox.prototype.getData = function () {
            var _this = this;
            if (this.mock || this.data) {
                return this.data;
            }
            else {
                var fInit = {
                    method: 'GET',
                    headers: new Headers(),
                    mode: 'cors',
                    cache: 'default'
                };
                fetch(this.url, fInit)
                    .then(function (resp) { return resp.json(); })
                    .then(function (data) {
                    _this.data = data;
                });
            }
        };
        RHDPSearchOneBox.prototype.getFeature = function () {
            var len = this.data && this.data['features'] ? this.data['features'].length : 0, f;
            for (var i = 0; i < len; i++) {
                if (this.data['features'][i].match.indexOf(this.term.toLowerCase()) >= 0) {
                    f = this.data['features'][i];
                }
            }
            return f;
        };
        RHDPSearchOneBox.prototype.getIcon = function (name) {
            var icons = {
                icon_help: '<i class="fas fa-question fa-lg"></i>',
                icon_helloworld: '<i class="fas fa-list-ol fa-lg"></i>',
                icon_docsandapi: '<i class="fas fa-file-alt fa-lg"></i>'
            };
            return name ? icons[name] : '';
        };
        return RHDPSearchOneBox;
    }(HTMLElement));
    customElements.define('rhdp-search-onebox', RHDPSearchOneBox);
    var templateObject_1$4, templateObject_2$2;

    var __extends$8 = (undefined && undefined.__extends) || (function () {
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
    var __makeTemplateObject$5 = (undefined && undefined.__makeTemplateObject) || function (cooked, raw) {
        if (Object.defineProperty) { Object.defineProperty(cooked, "raw", { value: raw }); } else { cooked.raw = raw; }
        return cooked;
    };
    var RHDPSearchResult = (function (_super) {
        __extends$8(RHDPSearchResult, _super);
        function RHDPSearchResult() {
            var _this = _super.call(this) || this;
            _this.template = function (strings, url, title, kind, created, description, premium, thumbnail) {
                return "<div>\n            <h4>" + (url ? "<a href=\"" + url + "\">" + title + "</a>" : title) + "</h4>\n            <p " + (premium ? 'class="result-info subscription-required" data-tooltip="" title="Subscription Required" data-options="disable-for-touch:true"' : 'class="result-info"') + ">\n                <span class=\"caps\">" + kind + "</span>\n                " + (created ? "- <pfe-datetime datetime=\"" + created + "\" type=\"local\" day=\"numeric\" month=\"long\" year=\"numeric\">" + created + "</pfe-datetime>" : '') + "\n            </p>\n            <p class=\"result-description\">" + description + "</p>\n        </div>\n        " + (thumbnail ? "<div class=\"thumb\"><img src=\"" + thumbnail.replace('http:', 'https:') + "\"></div>" : '');
            };
            return _this;
        }
        Object.defineProperty(RHDPSearchResult.prototype, "url", {
            get: function () {
                var stage = window.location.href.indexOf('stage') >= 0 || window.location.href.indexOf('developers') < 0 ? '.stage' : '';
                return !this.premium ? this._url : "https://developers" + stage + ".redhat.com/download-manager/link/3867444?redirect=" + encodeURIComponent(this._url);
            },
            set: function (val) {
                if (this._url === val)
                    return;
                this._url = val;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchResult.prototype, "title", {
            get: function () {
                return this._title;
            },
            set: function (val) {
                if (this._title === val)
                    return;
                this._title = val;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchResult.prototype, "kind", {
            get: function () {
                return this._kind;
            },
            set: function (val) {
                if (this._kind === val)
                    return;
                this._kind = val;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchResult.prototype, "created", {
            get: function () {
                return this._created;
            },
            set: function (val) {
                if (this._created === val)
                    return;
                this._created = val;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchResult.prototype, "description", {
            get: function () {
                return this._description;
            },
            set: function (val) {
                if (this._description === val)
                    return;
                this._description = val.replace('>', '&gt;').replace('<', '&lt;');
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchResult.prototype, "premium", {
            get: function () {
                return this._premium;
            },
            set: function (val) {
                if (this._premium === val)
                    return;
                this._premium = val;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchResult.prototype, "thumbnail", {
            get: function () {
                return this._thumbnail;
            },
            set: function (val) {
                if (this._thumbnail === val)
                    return;
                this._thumbnail = val;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchResult.prototype, "result", {
            get: function () {
                return this._result;
            },
            set: function (val) {
                if (this._result === val)
                    return;
                this._result = val;
                this.computeTitle(val);
                this.computeKind(val);
                this.computeCreated(val);
                this.computeDescription(val);
                this.computeURL(val);
                this.computePremium(val);
                this.computeThumbnail(val);
                this.renderResult();
            },
            enumerable: true,
            configurable: true
        });
        RHDPSearchResult.prototype.connectedCallback = function () {
        };
        Object.defineProperty(RHDPSearchResult, "observedAttributes", {
            get: function () {
                return ['result'];
            },
            enumerable: true,
            configurable: true
        });
        RHDPSearchResult.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
        };
        RHDPSearchResult.prototype.renderResult = function () {
            this.innerHTML = this.template(templateObject_1$5 || (templateObject_1$5 = __makeTemplateObject$5(["", "", "", "", "", "", "", ""], ["", "", "", "", "", "", "", ""])), this.url, this.title, this.kind, this.created, this.description, this.premium, this.thumbnail);
        };
        RHDPSearchResult.prototype.computeThumbnail = function (result) {
            if (result.fields.thumbnail) {
                this.thumbnail = result.fields.thumbnail[0];
            }
        };
        RHDPSearchResult.prototype.computeTitle = function (result) {
            var title = '';
            if (result.highlight && result.highlight.sys_title) {
                title = result.highlight.sys_title[0];
            }
            else {
                title = result.fields.sys_title[0];
            }
            this.title = title;
        };
        RHDPSearchResult.prototype.computeKind = function (result) {
            var kind = result.fields.sys_type || "webpage", map = {
                jbossdeveloper_archetype: 'Archetype',
                article: 'Article',
                blogpost: 'Blog Post',
                jbossdeveloper_bom: 'Bom',
                book: 'Book',
                cheatsheet: 'Cheat Sheet',
                demo: 'Demo',
                event: 'Event',
                forumthread: 'Forum Thread',
                jbossdeveloper_example: 'Demo',
                quickstart: 'Quickstart',
                quickstart_early_access: 'Demo',
                solution: 'Article',
                stackoverflow_thread: 'Stack Overflow',
                video: 'Video',
                webpage: 'Web Page',
                website: 'Web Page'
            };
            this.kind = map[kind] || 'Web Page';
        };
        RHDPSearchResult.prototype.computeCreated = function (result) {
            this.created = result.fields.sys_created && result.fields.sys_created.length > 0 ? result.fields.sys_created[0] : '';
        };
        RHDPSearchResult.prototype.computeDescription = function (result) {
            var description = '';
            if (result.highlight && result.highlight.sys_description) {
                description = result.highlight.sys_description[0];
            }
            else if (result.highlight && result.highlight.sys_content_plaintext) {
                description = result.highlight.sys_content_plaintext[0];
            }
            else if (result.fields && result.fields.sys_description) {
                description = result.fields.sys_description[0];
            }
            else if (result.fields && result.fields.sys_content_plaintext) {
                description = result.fields.sys_content_plaintext[0];
            }
            var tempDiv = document.createElement("div");
            tempDiv.innerHTML = description;
            description = tempDiv.innerText;
            this.description = description;
        };
        RHDPSearchResult.prototype.computeURL = function (result) {
            if (result.fields && result.fields.sys_type === 'book' && result.fields.field_book_url) {
                this.url = result.fields.field_book_url ? result.fields.field_book_url : '';
            }
            else {
                this.url = (result.fields && result.fields.sys_url_view) ? result.fields.sys_url_view : '';
            }
        };
        RHDPSearchResult.prototype.computePremium = function (result) {
            var premium = false;
            if (result._type === "rht_knowledgebase_article" || result._type === "rht_knowledgebase_solution") {
                premium = true;
            }
            this.premium = premium;
        };
        return RHDPSearchResult;
    }(HTMLElement));
    customElements.define('rhdp-search-result', RHDPSearchResult);
    var templateObject_1$5;

    var __extends$9 = (undefined && undefined.__extends) || (function () {
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
    var RHDPSearchResults = (function (_super) {
        __extends$9(RHDPSearchResults, _super);
        function RHDPSearchResults() {
            var _this = _super.call(this) || this;
            _this._more = false;
            _this._last = 0;
            _this._valid = true;
            _this.invalidMsg = document.createElement('div');
            _this.loadMore = document.createElement('div');
            _this.endOfResults = document.createElement('div');
            _this.loading = document.createElement('div');
            _this._renderResults = _this._renderResults.bind(_this);
            _this._setLoading = _this._setLoading.bind(_this);
            _this._checkValid = _this._checkValid.bind(_this);
            _this._clearResults = _this._clearResults.bind(_this);
            return _this;
        }
        Object.defineProperty(RHDPSearchResults.prototype, "results", {
            get: function () {
                return this._results;
            },
            set: function (val) {
                if (this._results === val)
                    return;
                this._results = val;
                this._renderResults(false);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchResults.prototype, "more", {
            get: function () {
                return this._more;
            },
            set: function (val) {
                if (this._more === val)
                    return;
                this._more = val;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchResults.prototype, "last", {
            get: function () {
                return this._last;
            },
            set: function (val) {
                if (this._last === val)
                    return;
                this._last = val ? val : 0;
                this.setAttribute('last', val.toString());
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchResults.prototype, "valid", {
            get: function () {
                return this._valid;
            },
            set: function (val) {
                if (this._valid === val)
                    return;
                this._valid = val;
            },
            enumerable: true,
            configurable: true
        });
        RHDPSearchResults.prototype.connectedCallback = function () {
            var _this = this;
            this.invalidMsg.className = 'invalidMsg';
            this.invalidMsg.innerHTML = "<h4>Well, this is awkward. No search term was entered yet, so this page is a little empty right now.</h4>\n        <p>After you enter a search term in the box above, you will see the results displayed here. \n        You can also use the filters to select a content type, product or topic to see some results too. Try it out!</p>";
            this.endOfResults.innerHTML = '<p class="end-of-results">- End of Results -</p>';
            this.loadMore.className = 'rhd-c-more-button';
            this.loadMore.innerHTML = '<a class="pf-c-button pf-m-primary" href="#">Load More</a>';
            this.loading.className = 'loading';
            this.loadMore.addEventListener('click', function (e) {
                e.preventDefault();
                _this.dispatchEvent(new CustomEvent('load-more', {
                    detail: {
                        from: _this.last
                    },
                    bubbles: true
                }));
            });
            top.addEventListener('search-complete', this._renderResults);
            top.addEventListener('search-start', this._setLoading);
            top.addEventListener('params-ready', this._checkValid);
            top.window.addEventListener('popstate', this._clearResults);
            this.addEventListener('load-more', function (e) {
                _this.more = true;
            });
        };
        RHDPSearchResults.prototype.addResult = function (result) {
            var item = new RHDPSearchResult();
            item.result = result;
            this.appendChild(item);
        };
        RHDPSearchResults.prototype._setLoading = function (e) {
            if (!this.more) {
                while (this.firstChild) {
                    this.removeChild(this.firstChild);
                }
            }
            else {
                if (this.querySelector('rhd-c-more-button')) {
                    this.removeChild(this.loadMore);
                }
                if (this.querySelector('.invalidMsg')) {
                    this.removeChild(this.invalidMsg);
                }
                this.more = false;
            }
            this.appendChild(this.loading);
        };
        RHDPSearchResults.prototype._renderResults = function (e) {
            if (this.querySelector('.loading')) {
                this.removeChild(this.loading);
            }
            if (e.detail && typeof e.detail.results !== 'undefined' && typeof e.detail.invalid === 'undefined') {
                this.addResults(e.detail.results);
            }
            else {
                while (this.firstChild) {
                    this.removeChild(this.firstChild);
                }
                this.appendChild(this.invalidMsg);
            }
            this.dispatchEvent(new CustomEvent('results-loaded', {
                detail: { results: this.results },
                bubbles: true
            }));
        };
        RHDPSearchResults.prototype._clearResults = function (e) {
            this.results = undefined;
        };
        RHDPSearchResults.prototype._checkValid = function (e) {
            var obj = e.detail;
            this.valid = Object.keys(obj.filters).length > 0 || (obj.term !== null && obj.term !== '' && typeof obj.term !== 'undefined');
            if (!this.valid) {
                this.appendChild(this.invalidMsg);
            }
            else {
                if (this.querySelector('.invalidMsg')) {
                    this.removeChild(this.invalidMsg);
                }
            }
        };
        RHDPSearchResults.prototype.addResults = function (results) {
            if (results && results.hits && results.hits.hits) {
                var hits = results.hits.hits;
                var l = hits.length;
                for (var i = 0; i < l; i++) {
                    this.addResult(hits[i]);
                }
                this.last = this.last + l;
                if (this.last >= results.hits.total) {
                    this.appendChild(this.endOfResults);
                }
                if (l > 0 && this.last < results.hits.total) {
                    if (this.querySelector('.end-of-results')) {
                        this.removeChild(this.endOfResults);
                    }
                    this.appendChild(this.loadMore);
                }
                else {
                    if (this.querySelector('.rhd-c-more-button')) {
                        this.removeChild(this.loadMore);
                    }
                    this.appendChild(this.endOfResults);
                }
            }
        };
        return RHDPSearchResults;
    }(HTMLElement));
    customElements.define('rhdp-search-results', RHDPSearchResults);

    var __extends$a = (undefined && undefined.__extends) || (function () {
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
    var RHDPSearchSortPage = (function (_super) {
        __extends$a(RHDPSearchSortPage, _super);
        function RHDPSearchSortPage() {
            var _this = _super.call(this) || this;
            _this.template = "\n        <div class=\"rhd-c-select\">\n            <select>\n              <option value=\"relevance\" selected>Sort by Relevance</option>\n              <option value=\"most-recent\">Sort by Most Recent</option>\n            </select>\n            <i class=\"fas fa-caret-down\"></i>\n        </div>";
            _this._sortChange = _this._sortChange.bind(_this);
            return _this;
        }
        Object.defineProperty(RHDPSearchSortPage.prototype, "sort", {
            get: function () {
                return this._sort;
            },
            set: function (val) {
                if (this._sort === val)
                    return;
                this._sort = val;
                this.setAttribute('sort', this._sort);
                this.querySelector('select').value = val;
            },
            enumerable: true,
            configurable: true
        });
        RHDPSearchSortPage.prototype.connectedCallback = function () {
            this.innerHTML = this.template;
            top.addEventListener('params-ready', this._sortChange);
            this.querySelector('select').onchange = this._sortChange;
        };
        Object.defineProperty(RHDPSearchSortPage, "observedAttributes", {
            get: function () {
                return ['sort'];
            },
            enumerable: true,
            configurable: true
        });
        RHDPSearchSortPage.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
        };
        RHDPSearchSortPage.prototype._sortChange = function (e) {
            if (e.detail && e.detail.sort) {
                this.sort = e.detail.sort;
            }
            else {
                if (e.target['options'] && typeof e.target['selectedIndex'] !== 'undefined') {
                    this.sort = e.target['options'][e.target['selectedIndex']].value;
                    this.dispatchEvent(new CustomEvent('sort-change', {
                        detail: {
                            sort: this.sort
                        },
                        bubbles: true
                    }));
                }
            }
        };
        return RHDPSearchSortPage;
    }(HTMLElement));
    customElements.define('rhdp-search-sort-page', RHDPSearchSortPage);

    var __extends$b = (undefined && undefined.__extends) || (function () {
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
    var RHDPSearchApp = (function (_super) {
        __extends$b(RHDPSearchApp, _super);
        function RHDPSearchApp() {
            var _this = _super.call(this) || this;
            _this._name = 'Search';
            _this._oburl = '../rhdp-apps/onebox/onebox.json';
            _this.template = "<div class=\"rhd-c-search-page\">\n    <span class=\"rhd-c-search-outage-message search-outage-msg\"></span>\n    <div class=\"searchpage-middle\">\n        <div class=\"rhd-c-search-page-header\">\n          <h2>" + _this.name + "</h2></div>\n        </div>\n        <div class=\"rhd-c-search-body pf-l-grid pf-m-gutter\">\n            <div class=\"rhd-c-search-body-left pf-l-grid__item pf-m-4-col-on-md pf-m-12-col-on-sm\"></div>\n            <div class=\"rhd-c-search-body-right pf-l-grid__item pf-m-8-col-on-md pf-m-12-col-on-sm\"></div>\n        </div>\n    </div>\n    <a href=\"#top\" id=\"scroll-to-top\"><i class=\"fas fa-arrow-circle-up\"></i></a>\n    </div>";
            _this.urlEle = new RHDPSearchURL();
            _this.query = new RHDPSearchQuery();
            _this.box = new RHDPSearchBox();
            _this.count = new RHDPSearchResultCount();
            _this.filters = new RHDPSearchFilters();
            _this.active = new RHDPSearchFilters();
            _this.modal = new RHDPSearchFilters();
            _this.onebox = new RHDPSearchOneBox();
            _this.results = new RHDPSearchResults();
            _this.sort = new RHDPSearchSortPage();
            _this.filterObj = {
                term: '',
                facets: [
                    {
                        name: 'Topics', key: 'tag', items: [
                            { key: 'dotnet', name: '.NET', value: ['dotnet', '.net', 'visual studio', 'c#'] },
                            { key: 'containers', name: 'Containers', value: ['atomic', 'cdk', 'containers'] },
                            { key: 'devops', name: 'DevOps', value: ['devops', 'CI', 'CD', 'Continuous Delivery'] },
                            { key: 'enterprise-java', name: 'Enterprise Java', value: ['ActiveMQ', 'AMQP', 'apache camel', 'Arquillian', 'Camel', 'CDI', 'CEP', 'CXF', 'datagrid', 'devstudio', 'Drools', 'Eclipse', 'fabric8', 'Forge', 'fuse', 'Hawkular', 'Hawtio', 'Hibernate', 'Hibernate ORM', 'Infinispan', 'iPaas', 'java ee', 'JavaEE', 'JBDS', 'JBoss', 'JBoss BPM Suite', 'Red Hat Decision Manager', 'JBoss Data Grid', 'jboss eap', 'JBoss EAP', ''] },
                            { key: 'iot', name: 'Internet of Things', value: ['iot', 'Internet of Things'] },
                            { key: 'microservices', name: 'Microservices', value: ['microservices', ' WildFly Swarm'] },
                            { key: 'mobile', name: 'Mobile', value: ['mobile', 'Red Hat Mobile', 'RHMAP', 'Cordova', 'FeedHenry'] },
                            { key: 'web-and-api-development', name: 'Web and API Development', value: ['Web', 'API', 'HTML5', 'REST', 'Camel', 'Node.js', 'RESTEasy', 'JAX-RS', 'Tomcat', 'nginx', 'Rails', 'Drupal', 'PHP', 'Bottle', 'Flask', 'Laravel', 'Dancer', 'Zope', 'TurboGears', 'Sinatra', 'httpd', 'Passenger'] },
                        ]
                    },
                    {
                        name: 'Content type', key: 'type', items: [
                            { key: 'apidocs', name: 'APIs and Docs', value: ['rht_website', 'rht_apidocs'], type: ['apidocs'] },
                            { key: 'archetype', name: 'Archetype', value: ['jbossdeveloper_archetype'], type: ['jbossdeveloper_archetype'] },
                            { key: 'article', name: 'Article', value: ['rht_knowledgebase_article', 'rht_knowledgebase_solution'], type: ['rht_knowledgebase_article', 'rht_knowledgebase_solution'] },
                            { key: 'blogpost', name: "Blog Posts", value: ['jbossorg_blog'], type: ['jbossorg_blog'] },
                            { key: 'book', name: "Book", value: ["jbossdeveloper_book"], type: ["jbossdeveloper_book"] },
                            { key: 'bom', name: "BOM", value: ["jbossdeveloper_bom"], type: ['jbossdeveloper_bom'] },
                            { key: 'cheatsheet', name: "Cheat Sheet", value: ['jbossdeveloper_cheatsheet'], type: ['jbossdeveloper_cheatsheet'] },
                            { key: 'demo', name: 'Demo', value: ['jbossdeveloper_demo'], type: ['jbossdeveloper_demo'] },
                            { key: 'event', name: 'Event', value: ['jbossdeveloper_event'], type: ['jbossdeveloper_event'] },
                            { key: 'forum', name: 'Forum', value: ['jbossorg_sbs_forum'], type: ['jbossorg_sbs_forum'] },
                            { key: 'get-started', name: "Get Started", value: ["jbossdeveloper_example"], type: ['jbossdeveloper_example'] },
                            { key: 'quickstart', name: "Quickstart", value: ['jbossdeveloper_quickstart'], type: ['jbossdeveloper_quickstart'] },
                            { key: 'stackoverflow', name: 'Stack Overflow', value: ['stackoverflow_question'], type: ['stackoverflow_question'] },
                            { key: 'video', name: "Video", value: ['jbossdeveloper_vimeo', 'jbossdeveloper_youtube'], type: ['jbossdeveloper_vimeo', 'jbossdeveloper_youtube'] },
                            { key: 'webpage', name: "Web Page", value: ['rht_website'], type: ['rht_website'] }
                        ]
                    },
                    {
                        name: 'Products &amp; Project',
                        key: 'project',
                        items: [
                            { key: 'dotnet1', name: '.NET Runtime for Red Hat Enterprise Linux', value: ['dotnet'] },
                            { key: 'amq', name: 'JBoss A-MQ', value: ['amq'] },
                            { key: 'rhpam', name: 'Red Hat Process Automation Manager', value: ['rhpam', 'bpmsuite'] },
                            { key: 'brms', name: 'Red Hat Decision Manager', value: ['brms'] },
                            { key: 'datagrid', name: 'JBoss Data Grid', value: ['datagrid'] },
                            { key: 'datavirt', name: 'JBoss Data Virtualization', value: ['datavirt'] },
                            { key: 'devstudio', name: 'JBoss Developer Studio', value: ['devstudio'] },
                            { key: 'eap', name: 'JBoss Enterprise Application Platform', value: ['eap'] },
                            { key: 'fuse', name: 'JBoss Fuse', value: ['fuse'] },
                            { key: 'webserver', name: 'JBoss Web Server', value: ['webserver'] },
                            { key: 'openjdk', name: 'OpenJDK', value: ['openjdk'] },
                            { key: 'rhamt', name: 'Red Hat Application Migration Toolkit', value: ['rhamt'] },
                            { key: 'cdk', name: 'Red Hat Container Development Kit', value: ['cdk'] },
                            { key: 'developertoolset', name: 'Red Hat Developer Toolset', value: ['developertoolset'] },
                            { key: 'devsuite', name: 'Red Hat Development Suite', value: ['devsuite'] },
                            { key: 'rhel', name: 'Red Hat Enterprise Linux', value: ['rhel'] },
                            { key: 'mobileplatform', name: 'Red Hat Mobile Application Platform', value: ['mobileplatform'] },
                            { key: 'openshift', name: 'Red Hat OpenShift Container Platform', value: ['openshift'] },
                            { key: 'softwarecollections', name: 'Red Hat Software Collections', value: ['softwarecollections'] }
                        ]
                    }
                ]
            };
            return _this;
        }
        Object.defineProperty(RHDPSearchApp.prototype, "name", {
            get: function () {
                return this._name;
            },
            set: function (val) {
                if (this._name === val)
                    return;
                this._name = val;
                this.setAttribute('name', this.name);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchApp.prototype, "url", {
            get: function () {
                return this._url;
            },
            set: function (val) {
                if (this._url === val)
                    return;
                this._url = val;
                this.query.url = this.url;
                this.setAttribute('url', this.url);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPSearchApp.prototype, "oburl", {
            get: function () {
                return this._oburl;
            },
            set: function (val) {
                if (this._oburl === val)
                    return;
                this._oburl = val;
            },
            enumerable: true,
            configurable: true
        });
        RHDPSearchApp.prototype.connectedCallback = function () {
            this.innerHTML = this.template;
            this.active.setAttribute('type', 'active');
            this.active.title = 'Active Filters:';
            this.modal.setAttribute('type', 'modal');
            this.modal.filters = this.filterObj;
            this.active.filters = this.filterObj;
            this.filters.filters = this.filterObj;
            this.query.filters = this.filterObj;
            this.onebox.url = this.oburl;
            document.body.appendChild(this.modal);
            this.querySelector('.rhd-c-search-page-header').appendChild(this.query);
            this.querySelector('.rhd-c-search-page-header').appendChild(this.box);
            this.querySelector('.rhd-c-search-body-left').appendChild(this.filters);
            this.querySelector('.rhd-c-search-body-right').appendChild(this.active);
            this.querySelector('.rhd-c-search-body-right').appendChild(this.sort);
            this.querySelector('.rhd-c-search-body-right').appendChild(this.count);
            this.querySelector('.rhd-c-search-body-right').appendChild(this.onebox);
            this.querySelector('.rhd-c-search-body-right').appendChild(this.results);
            document.body.appendChild(this.urlEle);
        };
        Object.defineProperty(RHDPSearchApp, "observedAttributes", {
            get: function () {
                return ['url', 'name', 'oburl'];
            },
            enumerable: true,
            configurable: true
        });
        RHDPSearchApp.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
        };
        RHDPSearchApp.prototype.toggleModal = function (e) {
            this.modal.toggle = e.detail.toggle;
        };
        RHDPSearchApp.prototype.updateSort = function (e) {
            this.query.sort = e.detail.sort;
            this.query.from = 0;
            this.results.last = 0;
            this.count.term = this.box.term;
        };
        return RHDPSearchApp;
    }(HTMLElement));
    customElements.define('rhdp-search-app', RHDPSearchApp);

    new RHDPSearchApp();
    new RHDPSearchBox();
    new RHDPSearchFilterGroup();
    new RHDPSearchFilterItem();
    new RHDPSearchFilters();
    new RHDPSearchOneBox();
    new RHDPSearchQuery();
    new RHDPSearchResultCount();
    new RHDPSearchResult();
    new RHDPSearchResults();
    new RHDPSearchSortPage();
    new RHDPSearchURL();

})));
