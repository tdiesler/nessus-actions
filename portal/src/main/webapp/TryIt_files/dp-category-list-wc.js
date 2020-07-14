(function (global, factory) {
    typeof exports === 'object' && typeof module !== 'undefined' ? factory(require('@patternfly/pfelement/pfelement.umd')) :
    typeof define === 'function' && define.amd ? define(['@patternfly/pfelement/pfelement.umd'], factory) :
    (global = global || self, factory(global.PFElement));
}(this, (function (PFElement) { 'use strict';

    PFElement = PFElement && PFElement.hasOwnProperty('default') ? PFElement['default'] : PFElement;

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
    var DPCategoryItemList = (function (_super) {
        __extends(DPCategoryItemList, _super);
        function DPCategoryItemList() {
            var _this = _super.call(this, DPCategoryItemList) || this;
            _this._index = 1;
            _this._visible = false;
            return _this;
        }
        Object.defineProperty(DPCategoryItemList.prototype, "html", {
            get: function () {
                return "\n            <style>\n            :host[visible] {\n                display: block;\n            }\n\n            :host {\n                display: none;\n                flex: 1 1 100%;\n                grid-column: span 1;\n            }\n\n            div {\n                background: white;\n                display: grid;\n                grid-template-columns: 1fr;\n                grid-gap: 15px;\n                position: relative;\n                padding-top: 15px;\n                padding-right: 15px;\n                padding-left: 15px;\n            }\n\n            @media (min-width: 500px) {\n                :host {\n                    grid-column: span 2;\n                    margin-bottom: 30px;\n                }\n\n                div {\n                    border: 1px solid #CCCCCC;\n                }\n            }\n\n            @media (min-width: 800px) {\n                :host {\n                    grid-column: span 3;\n                }\n\n                div {\n                    grid-template-columns: repeat(2, 1fr);\n                }\n            }\n\n            @media (min-width: 1200px) {\n                :host {\n                    grid-column: span 4;\n                }\n\n                div {\n                    grid-template-columns: repeat(3, 1fr);\n                    grid-gap: 30px;\n                    background-color: #FFFFFF;\n                    padding: 30px;\n                    margin-bottom: 30px;\n                }\n            }\n            </style>\n            <div>\n            <slot></slot>\n            </div>\n            ";
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DPCategoryItemList, "tag", {
            get: function () { return 'dp-category-item-list'; },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DPCategoryItemList.prototype, "index", {
            get: function () {
                return this._index;
            },
            set: function (val) {
                if (this._index === val)
                    return;
                this._index = val;
                _super.prototype.render.call(this);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DPCategoryItemList.prototype, "visible", {
            get: function () {
                return this._visible;
            },
            set: function (val) {
                val = val !== null && val !== false ? true : false;
                if (this._visible === val)
                    return;
                this._visible = val;
                if (this._visible) {
                    this.style.display = 'block';
                    this.setAttribute('visible', '');
                }
                else {
                    this.style.display = 'none';
                    this.removeAttribute('visible');
                }
            },
            enumerable: true,
            configurable: true
        });
        DPCategoryItemList.prototype.connectedCallback = function () {
            _super.prototype.connectedCallback.call(this);
        };
        Object.defineProperty(DPCategoryItemList, "observedAttributes", {
            get: function () {
                return ['index', 'visible'];
            },
            enumerable: true,
            configurable: true
        });
        DPCategoryItemList.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
        };
        return DPCategoryItemList;
    }(PFElement));
    PFElement.create(DPCategoryItemList);

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
    var DPCategoryItem = (function (_super) {
        __extends$1(DPCategoryItem, _super);
        function DPCategoryItem() {
            return _super.call(this, DPCategoryItem) || this;
        }
        Object.defineProperty(DPCategoryItem.prototype, "html", {
            get: function () {
                return "\n            <style>\n            \n            </style>\n            <slot></slot>\n            ";
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DPCategoryItem, "tag", {
            get: function () { return 'dp-category-item'; },
            enumerable: true,
            configurable: true
        });
        DPCategoryItem.prototype.connectedCallback = function () {
            _super.prototype.connectedCallback.call(this);
        };
        Object.defineProperty(DPCategoryItem, "observedAttributes", {
            get: function () {
                return ['url', 'name'];
            },
            enumerable: true,
            configurable: true
        });
        DPCategoryItem.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
        };
        return DPCategoryItem;
    }(PFElement));
    PFElement.create(DPCategoryItem);

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
    var DPCategoryList = (function (_super) {
        __extends$2(DPCategoryList, _super);
        function DPCategoryList() {
            var _this = _super.call(this, DPCategoryList) || this;
            _this.items = [];
            _this.active = 0;
            return _this;
        }
        Object.defineProperty(DPCategoryList.prototype, "html", {
            get: function () {
                return "\n<style>\n    :host {\n        position: relative;\n        background-color: #F9F9F9;\n        padding: 30px 0 15px 0;\n        display: block;\n    }\n\n    section {\n        display: grid;\n        grid-template-columns: 1fr;\n        grid-template-rows: auto;\n        grid-auto-flow: row;\n        grid-gap: 0;\n        margin: 0;\n        max-width: 500px;\n    }\n\n    @media (min-width: 500px) {\n        section {\n            grid-template-columns: repeat(2, 1fr);\n            grid-column-gap: 15px;\n            margin: 0 15px;\n            max-width: 800px;\n            justify-items: center;\n        }\n    }\n\n    @media (min-width: 800px) {\n        section {\n            grid-template-columns: repeat(3, 1fr);\n            grid-column-gap: 30px;\n            margin: 0 30px;\n            max-width: 1200px;\n            justify-items: center;\n        }\n    }\n\n    @media (min-width: 1200px) {\n        section {\n            grid-template-columns: repeat(4, 1fr);\n            grid-column-gap: 30px;\n            margin: 0 auto;\n            max-width: 1260px;\n            justify-items: center;\n        }\n    }\n</style>\n<section >\n<slot></slot>\n</section>\n";
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DPCategoryList, "tag", {
            get: function () { return 'dp-category-list'; },
            enumerable: true,
            configurable: true
        });
        DPCategoryList.prototype.connectedCallback = function () {
            var _this = this;
            _super.prototype.connectedCallback.call(this);
            this.addEventListener('dp-category-selected', function (e) {
                var w = window.innerWidth;
                var cols = 4;
                if (w < 500) {
                    cols = 1;
                }
                else if (w < 800) {
                    cols = 2;
                }
                else if (w < 1200) {
                    cols = 3;
                }
                var detail = e['detail'];
                var len = _this.querySelectorAll('dp-category').length + 1;
                var calc = 1 + (Math.ceil(detail.index / cols) * cols);
                var idx = calc <= len ? calc : len;
                var list = _this.querySelector('dp-category-item-list[visible]');
                if (list) {
                    list.removeAttribute('visible');
                    _this.removeChild(list);
                }
                if (detail.index === _this.active) {
                    var a = _this.querySelector('dp-category[visible]');
                    if (a) {
                        a.appendChild(list);
                    }
                    _this.active = 0;
                }
                else {
                    if (_this.active > 0) {
                        var a = _this.querySelector("dp-category:nth-child(" + _this.active + ")");
                        if (a) {
                            a.removeAttribute('visible');
                            a.appendChild(list);
                        }
                        _this.active = 0;
                    }
                    _this.active = detail.index;
                    list = _this.querySelector("dp-category:nth-child(" + _this.active + ")").querySelector('dp-category-item-list');
                    if (idx < len) {
                        var rowEle = _this.querySelector("dp-category:nth-child(" + idx + ")");
                        _this.insertBefore(list, rowEle);
                    }
                    else {
                        _this.appendChild(list);
                    }
                    list.setAttribute('visible', '');
                }
            });
            this.querySelector('dp-category').setAttribute('visible', '');
        };
        Object.defineProperty(DPCategoryList, "observedAttributes", {
            get: function () {
                return ['url', 'name'];
            },
            enumerable: true,
            configurable: true
        });
        DPCategoryList.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
        };
        DPCategoryList.prototype._setVisibleCategories = function (index) {
        };
        return DPCategoryList;
    }(PFElement));
    PFElement.create(DPCategoryList);

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
    var __awaiter = (undefined && undefined.__awaiter) || function (thisArg, _arguments, P, generator) {
        function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
        return new (P || (P = Promise))(function (resolve, reject) {
            function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
            function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
            function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
            step((generator = generator.apply(thisArg, _arguments || [])).next());
        });
    };
    var __generator = (undefined && undefined.__generator) || function (thisArg, body) {
        var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
        return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
        function verb(n) { return function (v) { return step([n, v]); }; }
        function step(op) {
            if (f) throw new TypeError("Generator is already executing.");
            while (_) try {
                if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
                if (y = 0, t) op = [op[0] & 2, t.value];
                switch (op[0]) {
                    case 0: case 1: t = op; break;
                    case 4: _.label++; return { value: op[1], done: false };
                    case 5: _.label++; y = op[1]; op = [0]; continue;
                    case 7: op = _.ops.pop(); _.trys.pop(); continue;
                    default:
                        if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                        if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                        if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                        if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                        if (t[2]) _.ops.pop();
                        _.trys.pop(); continue;
                }
                op = body.call(thisArg, _);
            } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
            if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
        }
    };
    var DPCategory = (function (_super) {
        __extends$3(DPCategory, _super);
        function DPCategory() {
            var _this = _super.call(this, DPCategory, { delayRender: true }) || this;
            _this._visible = false;
            _this._index = -1;
            _this._showList = _this._showList.bind(_this);
            return _this;
        }
        Object.defineProperty(DPCategory.prototype, "html", {
            get: function () {
                return "\n<style>\n:host { \n    grid-column: span 1;\n    border-top: 1px solid var(--rhd-blue);\n    display: flex;\n    flex-direction: row;\n    flex-wrap: wrap;\n    padding: 15px;\n    align-items: center;\n    background-color: var(--rhd-white, #ffffff);\n    position: relative;\n    z-index: 1;\n}\n\nimg, svg { \n    flex: 0 0 60px; \n    padding-right: 24px; \n    height: 60px;   \n}\n\nh4 {\n    flex: 1 0 auto;\n    color: #0066CC;\n    font-family: \"Overpass\", \"Open Sans\", Arial, Helvetica, sans-serif;\n    font-size: 14px;\n    font-weight: normal;\n    line-height: 21px;\n    margin: 0 0 5px 0;\n}\n\n:host(:hover), :host([visible]) {\n    cursor: pointer;\n    color: var(--rhd-blue);\n    fill: var(--rhd-blue);\n    border-top: 5px solid var(--rhd-blue);\n    border-bottom: 5px solid var(--rhd-blue);\n}\n\n@media (min-width: 500px) {\n    :host, :host(:hover), :host([visible]) {\n        flex-direction: column;\n        text-align: center; \n        border-top: none;\n        border-bottom: none;\n        background-color: transparent;\n        margin-bottom:30px;\n    }\n\n    :host([visible]):after, :host([visible]):before {\n        top: 100%;\n        left: 50%;\n        border: solid transparent;\n        content: \" \";\n        height: 0;\n        width: 0;\n        position: absolute;\n        pointer-events: none;\n    }\n    \n    :host([visible]):before {\n        border-bottom-color: #CCCCCC;\n        border-width: 15px;\n        margin-left: -15px;\n    }\n    :host([visible]):after {\n        border-bottom-color: #FFFFFF;\n        border-width: 16px;\n        margin-left: -16px;\n    }\n    \n\n    img, svg { flex: 0 0 150px; height: 150px; padding-right: 0; padding-bottom: 15px; }\n}\n\n@media (min-width: 800px) {\n    :host {\n        \n    }\n}\n\n@media (min-width: 1200px) {\n    :host {\n        \n    }\n}\n</style>\n" + (this.image && this.image.indexOf('svg') < 0 ? "<img src=\"" + this.image + "\">" : this.image) + "\n<h4>" + this.name + "</h4>\n<slot></slot>\n";
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DPCategory, "tag", {
            get: function () { return 'dp-category'; },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DPCategory.prototype, "name", {
            get: function () { return this._name; },
            set: function (val) {
                if (this._name === val)
                    return;
                this._name = val;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DPCategory.prototype, "image", {
            get: function () { return this._image; },
            set: function (val) {
                if (this._image === val)
                    return;
                if (!val.match(/\.svg$/)) {
                    this._image = val;
                }
                else {
                    this._getSVG(val);
                }
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DPCategory.prototype, "visible", {
            get: function () { return this._visible; },
            set: function (val) {
                val = val !== null && val !== false ? true : false;
                if (this._visible === val)
                    return;
                this._visible = val;
                var evt = {
                    detail: {
                        index: this._getIndex(this)
                    },
                    bubbles: true,
                    composed: true
                };
                this.dispatchEvent(new CustomEvent('dp-category-selected', evt));
                if (this._visible) {
                    this.setAttribute('visible', '');
                }
                else {
                    this.removeAttribute('visible');
                }
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DPCategory.prototype, "index", {
            get: function () {
                return this._index;
            },
            set: function (val) {
                if (this._index === val)
                    return;
                this._index = val;
            },
            enumerable: true,
            configurable: true
        });
        DPCategory.prototype.connectedCallback = function () {
            var _this = this;
            _super.prototype.connectedCallback.call(this);
            this.addEventListener('click', function (e) {
                e.preventDefault();
                _this.visible = !_this.visible;
                return false;
            });
            _super.prototype.render.call(this);
        };
        Object.defineProperty(DPCategory, "observedAttributes", {
            get: function () {
                return ['name', 'image', 'visible'];
            },
            enumerable: true,
            configurable: true
        });
        DPCategory.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
        };
        DPCategory.prototype._showList = function () {
            this.visible = !this.visible;
        };
        DPCategory.prototype._getIndex = function (node) {
            if (this.index < 0) {
                var i = 1;
                while (node = node.previousElementSibling) {
                    if (node.nodeName === 'DP-CATEGORY') {
                        ++i;
                    }
                }
                return i;
            }
            else {
                return this.index;
            }
        };
        DPCategory.prototype._getSVG = function (path) {
            return __awaiter(this, void 0, void 0, function () {
                var resp, svg;
                return __generator(this, function (_a) {
                    switch (_a.label) {
                        case 0: return [4, fetch(path)];
                        case 1:
                            resp = _a.sent();
                            return [4, resp.text()];
                        case 2:
                            svg = _a.sent();
                            this.image = svg.substring(svg.indexOf('<svg'));
                            _super.prototype.render.call(this);
                            return [2];
                    }
                });
            });
        };
        return DPCategory;
    }(PFElement));
    PFElement.create(DPCategory);

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
    var DPProductShortTeaser = (function (_super) {
        __extends$4(DPProductShortTeaser, _super);
        function DPProductShortTeaser() {
            return _super.call(this, DPProductShortTeaser, { delayRender: true }) || this;
        }
        Object.defineProperty(DPProductShortTeaser.prototype, "html", {
            get: function () {
                return "\n<style>\n    :host { \n        font-family: \"Overpass\", \"Open Sans\", Arial, Helvetica, sans-serif;\n        font-size: 14px;\n        line-height: 21px;\n        margin-bottom: 30px;\n        display: flex;\n        flex-direction: column;\n        text-align: left;\n    }\n    h4 { \n        flex: 0 0 24px;\n        font-family: \"Overpass\", \"Open Sans\", Arial, Helvetica, sans-serif;\n        font-size: 14px;\n        font-weight: bold;\n        line-height: 24px;\n        margin: 0 0 5px 0;\n    }\n    h4 a {\n        color: #0066CC;\n        text-decoration: none;\n    }\n\n    div {\n        flex: 1 1 auto;\n        margin-bottom: 16px;\n        color: #000000;\n    }\n\n    a.more {\n        flex: 0 0 25px;\n        display: block;\n        width: auto;\n        color: #0066CC;\n        font-size: 16px;\n        line-height: 25px;\n    }\n</style>\n<h4><a href=\"" + this.link + "\">" + this.name + "</a></h4>\n<div>\n<slot></slot>\n</div>\n<a class=\"more\" href=\"" + this.downloadLink + "\">View all downloads <i class=\"fas fa-caret-right\"></i></a>\n        ";
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DPProductShortTeaser, "tag", {
            get: function () { return 'dp-product-short-teaser'; },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DPProductShortTeaser.prototype, "name", {
            get: function () {
                return this._name;
            },
            set: function (val) {
                if (this._name === val)
                    return;
                this._name = val;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DPProductShortTeaser.prototype, "link", {
            get: function () {
                return this._link;
            },
            set: function (val) {
                if (this._link === val)
                    return;
                this._link = val;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DPProductShortTeaser.prototype, "downloadLink", {
            get: function () {
                return this._downloadLink;
            },
            set: function (val) {
                if (this._downloadLink === val)
                    return;
                this._downloadLink = val;
            },
            enumerable: true,
            configurable: true
        });
        DPProductShortTeaser.prototype.connectedCallback = function () {
            _super.prototype.connectedCallback.call(this);
            _super.prototype.render.call(this);
        };
        Object.defineProperty(DPProductShortTeaser, "observedAttributes", {
            get: function () {
                return ['name', 'link', 'download-link'];
            },
            enumerable: true,
            configurable: true
        });
        DPProductShortTeaser.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            if (name !== 'download-link') {
                this[name] = newVal;
            }
            else {
                this.downloadLink = newVal;
            }
        };
        return DPProductShortTeaser;
    }(PFElement));
    PFElement.create(DPProductShortTeaser);

    new DPCategoryItemList();
    new DPCategoryItem();
    new DPCategoryList();
    new DPCategory();
    new DPProductShortTeaser();

})));
