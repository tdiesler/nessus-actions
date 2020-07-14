(function (global, factory) {
    typeof exports === 'object' && typeof module !== 'undefined' ? factory(exports, require('@patternfly/pfelement/pfelement.umd')) :
    typeof define === 'function' && define.amd ? define(['exports', '@patternfly/pfelement/pfelement.umd'], factory) :
    (global = global || self, factory(global.DPAlert = {}, global.PFElement));
}(this, (function (exports, PFElement) { 'use strict';

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
    var DPAlert = (function (_super) {
        __extends(DPAlert, _super);
        function DPAlert() {
            var _this = _super.call(this, DPAlert, { delayRender: true }) || this;
            _this._type = 'info';
            _this._icon = 'https://static.jboss.org/rhd/images/icons/RHD_alerticon_info.svg';
            _this._background = '#dcedf8';
            _this._border = '#87aac1';
            return _this;
        }
        Object.defineProperty(DPAlert.prototype, "html", {
            get: function () {
                return "\n        <style>\n        :host .pf-c-alert.pf-m-inline {\n            border-left: 3px solid rgb(0, 149, 150);\n        }\n        :host .pf-c-alert__icon {\n            --alert-color--background: #fff;\n            --alert-color--text: #003737;\n            background-color: var(--alert-color--background);\n            color: var(--alert-color--text);\n        }\n        :host .pf-c-alert__title {\n            --alert-color--text: #003737;\n            color: var(--alert-color--text);\n        }\n\n        :host .pf-screen-reader {\n            position: fixed;\n            overflow: hidden;\n            clip: rect(0,0,0,0);\n            white-space: nowrap;\n            border: 0;\n        }\n\n        .pf-c-alert {\n            display: grid;\n            grid-template-areas: \"icon title action\" \"icon content content\";\n            grid-template-columns: 31.75px auto 1fr;\n            grid-template-rows: 56px 0;\n            font-size: 16px;\n            font-weight: 400;\n            box-shadow: rgba(3, 3, 3, 0.13) 0px 3px 7px 3px, rgba(3, 3, 3, 0.12) 0px 11px 24px 16px;\n            background-color: #fff;\n            line-height: 24px;\n            margin: 0;\n            padding: 0;\n            text-align: left;\n            position: relative;\n        }\n\n        .pf-c-alert.pf-m-inline {\n            box-shadow: none;\n            background-clor: #fff;\n            border: solid #ededed;\n            border-width: 1px 1px 1px 0;\n        }\n\n        .pf-c-alert__title {\n            background-color: #fff;\n            color: #003737;\n            font-size: 16px;\n            font-weight: 700;\n            grid-column-end: title;\n            grid-column-start: title;\n            grid-row-end: title;\n            grid-row-start: title;\n            line-height: 24px;\n            margin: 0;\n            padding: 16px;\n        }\n\n        .pf-c-alert__description {\n            background-color: #fff;\n            color: #151515;\n            font-size: 16px;\n            font-weight: 400;\n            display: contents;\n            grid-column-end: content;\n            grid-column-start: content;\n            grid-row-end: content;\n            grid-row-start: content;\n            line-height: 24px;\n            margin-top: -8px;\n            margin-right: 0;\n            margin-bottom: 0;\n            margin-left: 0;\n            padding-top: 0;\n            padding-right: 16px;\n            padding-bottom: 16px;\n            padding-left: 16px;\n        }\n\n        .pf-c-alert__action {\n            background-color: #fff;\n            color: #151515;\n            font-size: 16px;\n            font-weight: 400;\n            grid-column-end: action;\n            grid-column-start: action;\n            grid-row-end: action;\n            grid-row-start: action;\n            line-height: 24px;\n            margin-top: 0;\n            margin-right: 0;\n            margin-bottom: 0;\n            margin-left: 0;\n            padding-top: 11px;\n            padding-right: 4px;\n            padding-bottom: 0;\n            padding-left: 0;\n            text-align: center;\n        }\n\n        .pf-c-alert__icon {\n            display: flex;\n            font-size: 18px;\n            font-weight: 400;\n            grid-column-end: icon;\n            grid-column-start: icon;\n            grid-row-end: icon;\n            grid-row-start: icon;\n            line-height: 36px;\n            margin: 0;\n            padding: 19px 0 16px 12px;\n            text-align: left;\n        }\n\n        .pf-c-alert__icon img {\n            width: 20px;\n        }\n\n        :host([type=\"success\"]) .pf-c-alert.pf-m-inline {\n            border-left: 3px solid rgb(146, 212, 0);\n        }\n        :host([type=\"success\"]) .pf-c-alert__icon {\n            --alert-color--background: #fff;\n            --alert-color--text: #486b00;\n            background-color: var(--alert-color--background);\n            color: var(--alert-color--text);\n        }:host([type=\"success\"]) .pf-c-alert__title {\n            --alert-color--text: #486b00;\n            color: var(--alert-color--text);\n        }\n\n        :host([type=\"warning\"]) .pf-c-alert.pf-m-inline {\n            border-left: 3px solid rgb(240, 171, 0);\n        }\n        :host([type=\"warning\"]) .pf-c-alert__icon {\n            --alert-color--background: #fff;\n            --alert-color--text: #795600;\n            background-color: var(--alert-color--background);\n            color: var(--alert-color--text);\n        }\n        :host([type=\"warning\"]) .pf-c-alert__title {\n            --alert-color--text: #795600;\n            color: var(--alert-color--text);\n        }\n\n        :host([type=\"error\"]) .pf-c-alert.pf-m-inline {\n            border-left: 3px solid rgb(201, 25, 11);\n        }\n        :host([type=\"error\"]) .pf-c-alert__icon {\n            --alert-color--background: #fff;\n            --alert-color--text: #470000;\n            background-color: var(--alert-color--background);\n            color: var(--alert-color--text);\n        }\n        :host([type=\"error\"]) .pf-c-alert__title {\n            --alert-color--text: #470000;\n            color: var(--alert-color--text);\n        }\n\n        :host([type=\"info\"]) .pf-c-alert.pf-m-inline {\n            border-left: 3px solid rgb(115, 188, 247);\n        }\n        :host([type=\"info\"]) .pf-c-alert__icon {\n            --alert-color--background: #fff;\n            --alert-color--text: #004368;\n            background-color: var(--alert-color--background);\n            color: var(--alert-color--text);\n        }\n        :host([type=\"info\"]) .pf-c-alert__title {\n            --alert-color--text: #004368;\n            color: var(--alert-color--text);\n        }\n\n        :host([size=\"xl\"]) .pf-c-alert {\n            grid-template-columns: 31.75px auto 47px;\n            grid-template-rows: 56px auto;\n        }\n\n        :host([size=\"xl\"]) .pf-c-alert__icon {\n            display: flex;\n            align-items: flex-start;\n        }\n        :host([size=\"xl\"]) .pf-c-alert__icon img {\n            width: 20px;\n        }\n        :host([size=\"xl\"]) .pf-c-alert__action a img {\n            width: 20px;\n        }\n\n        :host([size=\"xl\"]) .close {\n            grid-column: 3;\n            grid-row: 1;\n        }\n\n        :host([size=\"xl\"]) .pf-c-alert__description p {\n            padding-top: 0;\n            padding-right: 16px;\n            padding-bottom: 16px;\n            padding-left: 16px;\n            margin-top: 0;\n            margin-bottom: 0;\n        }\n\n        a.close {\n            margin-left: 5px;\n            background-repeat: no-repeat;\n            height: 24px;\n            color: #3b6e90;\n        }\n\n        </style>\n        <div class=\"pf-c-alert pf-m-inline\" aria=label=\"alert\">\n        <div class=\"pf-c-alert__icon\">\n            <img src=\"" + this.icon + "\">\n        </div>\n        " + (this.size === 'xl' ? '<h4 class="pf-c-alert__title">' : '') + "\n        " + (this.heading ? "<h4 class=\"pf-c-alert__title\"><span class=\"pf-screen-reader\">" + this.heading + "</span>" + this.heading + "</h4>" : '') + "\n        " + (this.size === 'xl' ? '</h4>' : '') + "\n        <div class=\"pf-c-alert__description\"><p><slot>" + this.text + "</slot></p></div>\n        " + (this.size === 'xl' ? "<div class=\"pf-c-alert__action\"><a class=\"close\" href=\"#\"><img src=\"https://static.jboss.org/rhd/images/icons/fa_times_icon.svg\"></a></div>" : '') + "\n        </div>";
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DPAlert, "tag", {
            get: function () { return 'dp-alert'; },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DPAlert.prototype, "type", {
            get: function () {
                return this._type;
            },
            set: function (val) {
                if (this._type === val)
                    return;
                this._type = val;
                switch (this._type) {
                    case 'success':
                        this.icon = 'https://static.jboss.org/rhd/images/icons/RHD_alerticon_success.svg';
                        break;
                    case 'warning':
                        this.icon = 'https://static.jboss.org/rhd/images/icons/RHD_alerticon_warning.svg';
                        break;
                    case 'error':
                        this.icon = 'https://static.jboss.org/rhd/images/icons/RHD_alerticon_error.svg';
                        break;
                    case 'info':
                    default:
                        this.icon = 'https://static.jboss.org/rhd/images/icons/RHD_alerticon_info.svg';
                        break;
                }
                this.setAttribute('type', this._type);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DPAlert.prototype, "size", {
            get: function () {
                return this._size;
            },
            set: function (val) {
                if (this._size === val)
                    return;
                this._size = val;
                this.setAttribute('size', this._size);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DPAlert.prototype, "heading", {
            get: function () {
                return this._heading;
            },
            set: function (val) {
                if (this._heading === val)
                    return;
                this._heading = val;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DPAlert.prototype, "text", {
            get: function () {
                return this._text;
            },
            set: function (val) {
                if (this._text === val)
                    return;
                this._text = val;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(DPAlert.prototype, "icon", {
            get: function () {
                return this._icon;
            },
            set: function (val) {
                if (this._icon === val)
                    return;
                this._icon = val;
            },
            enumerable: true,
            configurable: true
        });
        DPAlert.prototype.connectedCallback = function () {
            var _this = this;
            _super.prototype.connectedCallback.call(this);
            var closer = this.shadowRoot.querySelector('.close');
            if (closer) {
                this.addEventListener('click', function (e) {
                    e.preventDefault();
                    if (e.composedPath()[0]['className'] === 'close') {
                        _this.remove();
                    }
                });
            }
            _super.prototype.render.call(this);
        };
        Object.defineProperty(DPAlert, "observedAttributes", {
            get: function () {
                return ['type', 'size', 'heading'];
            },
            enumerable: true,
            configurable: true
        });
        DPAlert.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
            _super.prototype.render.call(this);
        };
        DPAlert.prototype.acknowledge = function () {
            this.remove();
        };
        return DPAlert;
    }(PFElement));
    PFElement.create(DPAlert);

    exports.DPAlert = DPAlert;

    Object.defineProperty(exports, '__esModule', { value: true });

})));
