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
    var RHDPOSDownload = (function (_super) {
        __extends(RHDPOSDownload, _super);
        function RHDPOSDownload() {
            var _this = _super !== null && _super.apply(this, arguments) || this;
            _this._rhelURL = "";
            _this._macURL = "";
            _this._winURL = "";
            _this.stage_download_url = 'https://developers.stage.redhat.com';
            _this.productDownloads = {
                "cdk": { "windowsUrl": "/download-manager/file/cdk-3.13.0-1-minishift-windows-amd64.exe", "macUrl": "/download-manager/file/cdk-3.13.0-1-minishift-darwin-amd64", "rhelUrl": "/download-manager/file/cdk-3.13.0-1-minishift-linux-amd64" }
            };
            _this.template = function (strings, product, downloadUrl, platform, version) {
                return "<div class=\"large-8 columns download-link\">\n                    <a class=\"pf-c-button pf-m-heavy\" href=\"" + downloadUrl + "\">\n                        <i class=\"fa fa-download\"></i> Download</a>\n                    <div class=\"version-name\">\n                        <span id=\"rhdp-os-dl-product\">" + product + "</span> \n                        <span id=\"rhdp-os-dl-version\">" + version + "</span> \n                        <span id=\"rhdp-os-dl-os\">" + (_this.displayOS ? "for <span id=\"rhdp-os-dl-platform\">" + platform + "</span></span>" : '') + "\n                    </div>\n                </div>\n                ";
            };
            _this.downloadsTemplate = function (strings, product, downloadUrl, platform, version) {
                return "<div class=\"large-8 columns download-link\">\n                    <a class=\"pf-c-button pf-m-heavy\" href=\"" + downloadUrl + "\">\n                        <i class=\"fa fa-download\"></i> Download</a>\n                    <div class=\"version-name\">\n                        <span id=\"rhdp-os-dl-product\">" + product + "</span> \n                        <span id=\"rhdp-os-dl-version\">" + version + "</span> \n                        " + (_this.displayOS ? "for <span id=\"rhdp-os-dl-platform\">" + platform + "</span>" : '') + "\n                    </div>\n                </div>\n                ";
            };
            return _this;
        }
        Object.defineProperty(RHDPOSDownload.prototype, "url", {
            get: function () {
                return this._url;
            },
            set: function (value) {
                if (this._url === value)
                    return;
                this._url = value;
                this.setAttribute('url', this._url);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPOSDownload.prototype, "productCode", {
            get: function () {
                return this._productCode;
            },
            set: function (value) {
                if (this._productCode === value)
                    return;
                this._productCode = value;
                this.setAttribute('product-code', this._productCode);
                this.render();
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPOSDownload.prototype, "platformType", {
            get: function () {
                return this._platformType;
            },
            set: function (value) {
                if (this._platformType === value)
                    return;
                this._platformType = value;
                this.setAttribute('platform-type', this._platformType);
                this.render();
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPOSDownload.prototype, "downloadURL", {
            get: function () {
                return this._downloadURL;
            },
            set: function (value) {
                if (this._downloadURL === value)
                    return;
                this._downloadURL = value;
                this.setAttribute('download-url', this._downloadURL);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPOSDownload.prototype, "rhelURL", {
            get: function () {
                return this._rhelURL;
            },
            set: function (value) {
                if (this._rhelURL === value)
                    return;
                this._rhelURL = value;
                this.setAttribute('rhel-download', this._rhelURL);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPOSDownload.prototype, "macURL", {
            get: function () {
                return this._macURL;
            },
            set: function (value) {
                if (this._macURL === value)
                    return;
                this._macURL = value;
                this.setAttribute('mac-download', this._macURL);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPOSDownload.prototype, "winURL", {
            get: function () {
                return this._winURL;
            },
            set: function (value) {
                if (this._winURL === value)
                    return;
                this._winURL = value;
                this.setAttribute('windows-download', this._winURL);
                this.render();
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPOSDownload.prototype, "productName", {
            get: function () {
                return this._productName;
            },
            set: function (value) {
                if (this._productName === value)
                    return;
                this._productName = value;
                this.setAttribute('name', this._productName);
                this.render();
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPOSDownload.prototype, "version", {
            get: function () {
                return this._version;
            },
            set: function (value) {
                if (this._version === value)
                    return;
                this._version = value;
                this.setAttribute('version', this._version);
                this.render();
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPOSDownload.prototype, "displayOS", {
            get: function () {
                return this._displayOS;
            },
            set: function (value) {
                if (this._displayOS === value)
                    return;
                this._displayOS = value;
                this.setAttribute('display-os', this._displayOS);
                this.render();
            },
            enumerable: true,
            configurable: true
        });
        RHDPOSDownload.prototype.connectedCallback = function () {
            this.platformType = this.getUserAgent();
            this.setDownloadURLByPlatform();
            this.render();
        };
        Object.defineProperty(RHDPOSDownload, "observedAttributes", {
            get: function () {
                return ['product-code', 'platform-type', 'download-url', 'name', 'version'];
            },
            enumerable: true,
            configurable: true
        });
        RHDPOSDownload.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            var m = {
                'product-code': 'productCode',
                'platform-type': 'platformType',
                'download-url': 'downloadURL',
                'name': 'productName',
                'version': 'version'
            };
            this[m[name]] = newVal;
        };
        RHDPOSDownload.prototype.render = function () {
            this.innerHTML = this.template(templateObject_1 || (templateObject_1 = __makeTemplateObject(["", "", "", "", ""], ["", "", "", "", ""])), this.productName, this.downloadURL, this.platformType, this.version);
        };
        RHDPOSDownload.prototype.getUserAgent = function () {
            var OSName = "Windows";
            if (navigator.appVersion.indexOf("Mac") != -1)
                OSName = "MacOS";
            if (navigator.appVersion.indexOf("Linux") != -1)
                OSName = "RHEL";
            return OSName;
        };
        RHDPOSDownload.prototype.getDownloadOrigin = function (productUrl) {
            if (window.location.origin.indexOf('developers.stage.redhat.com') > 0) {
                productUrl = productUrl.replace(/http(s)?:\/\/developers.redhat.com/g, this.stage_download_url);
            }
            return productUrl;
        };
        RHDPOSDownload.prototype.setOSURL = function (productId) {
            switch (productId) {
                case 'cdk':
                    this.winURL = this.getDownloadOrigin(this.productDownloads.cdk.windowsUrl);
                    this.macURL = this.getDownloadOrigin(this.productDownloads.cdk.macUrl);
                    this.rhelURL = this.getDownloadOrigin(this.productDownloads.cdk.rhelUrl);
                    break;
                default:
                    this.winURL = this.getDownloadOrigin(this.downloadURL);
                    this.macURL = this.getDownloadOrigin(this.downloadURL);
                    this.rhelURL = this.getDownloadOrigin(this.downloadURL);
            }
        };
        RHDPOSDownload.prototype.setDownloadURLByPlatform = function () {
            if (this.winURL.length <= 0 || this.macURL.length <= 0 || this.rhelURL.length <= 0) {
                return;
            }
            this.displayOS = true;
            switch (this.platformType) {
                case "Windows":
                    this.downloadURL = this.getDownloadOrigin(this.winURL);
                    break;
                case "MacOS":
                    this.downloadURL = this.getDownloadOrigin(this.macURL);
                    break;
                case "RHEL":
                    this.downloadURL = this.getDownloadOrigin(this.rhelURL);
                    break;
                default:
                    this.downloadURL = this.getDownloadOrigin(this.winURL);
            }
        };
        return RHDPOSDownload;
    }(HTMLElement));
    customElements.define('rhdp-os-download', RHDPOSDownload);
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
    var RHDPDownloadsAllItem = (function (_super) {
        __extends$1(RHDPDownloadsAllItem, _super);
        function RHDPDownloadsAllItem() {
            var _this = _super.call(this) || this;
            _this.template = function (strings, name, productId, dataFallbackUrl, downloadUrl, learnMore, description, version, platform) {
                return "\n            <div class=\"row\">\n                <hr>\n                <div class=\"large-24 column\">\n                    <h5>" + name + "</h5>\n                </div>\n            \n                <div class=\"large-10 columns\">\n                    <p></p>\n            \n                    <div class=\"paragraph\">\n                        <p>" + description + "</p>\n                    </div>\n                    <a href=\"" + learnMore + "\">Learn More</a></div>\n            \n                <div class=\"large-9 center columns\">\n                \n                  " + (version ? "<p data-download-id-version=\"" + productId + "\">Version: " + version + " " + (_this.platform ? "for " + platform : '') + "</p>" : "<p data-download-id-version=\"" + productId + "\">&nbsp;</p>") + "  \n                </div>\n            \n                <div class=\"large-5 columns\"><a class=\"pf-c-button pf-m-secondary\" data-download-id=\"" + productId + "\"\n                                                data-fallback-url=\"" + dataFallbackUrl + "\"\n                                                href=\"" + downloadUrl + "\">Download</a></div>\n            </div>\n";
            };
            return _this;
        }
        Object.defineProperty(RHDPDownloadsAllItem.prototype, "name", {
            get: function () {
                return this._name;
            },
            set: function (value) {
                if (this._name === value)
                    return;
                this._name = value;
                this.setAttribute('name', this.name);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPDownloadsAllItem.prototype, "productId", {
            get: function () {
                return this._productId;
            },
            set: function (value) {
                if (this.productId === value)
                    return;
                this._productId = value;
                this.setAttribute('productid', this._productId);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPDownloadsAllItem.prototype, "dataFallbackUrl", {
            get: function () {
                return this._dataFallbackUrl;
            },
            set: function (value) {
                if (this.dataFallbackUrl === value)
                    return;
                this._dataFallbackUrl = value;
                this.setAttribute('datafallbackurl', this._dataFallbackUrl);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPDownloadsAllItem.prototype, "downloadUrl", {
            get: function () {
                return this._downloadUrl;
            },
            set: function (value) {
                if (this.downloadUrl === value)
                    return;
                this._downloadUrl = value;
                this.setAttribute('downloadurl', this._downloadUrl);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPDownloadsAllItem.prototype, "description", {
            get: function () {
                return this._description;
            },
            set: function (value) {
                this._description = value;
                this.setAttribute('description', this._description);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPDownloadsAllItem.prototype, "learnMore", {
            get: function () {
                return this._learnMore;
            },
            set: function (value) {
                this._learnMore = value;
                this.setAttribute('learnmore', this._learnMore);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPDownloadsAllItem.prototype, "version", {
            get: function () {
                return this._version;
            },
            set: function (value) {
                this._version = value;
                this.setAttribute('version', this._version);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPDownloadsAllItem.prototype, "platform", {
            get: function () {
                return this._platform;
            },
            set: function (value) {
                this._platform = value;
                this.setAttribute('platform', this._platform);
            },
            enumerable: true,
            configurable: true
        });
        RHDPDownloadsAllItem.prototype.connectedCallback = function () {
            if (this.productId === 'cdk') {
                this.osVersionExtract(this.productId);
                this.innerHTML = this.template(templateObject_1$1 || (templateObject_1$1 = __makeTemplateObject$1(["", "", "", "", "", "", "", "", ""], ["", "", "", "", "", "", "", "", ""])), this.name, this.productId, this.dataFallbackUrl, this.downloadUrl, this.learnMore, this.description, this.version, this.platform);
            }
            else {
                this.innerHTML = this.template(templateObject_2 || (templateObject_2 = __makeTemplateObject$1(["", "", "", "", "", "", "", "", ""], ["", "", "", "", "", "", "", "", ""])), this.name, this.productId, this.dataFallbackUrl, this.downloadUrl, this.learnMore, this.description, this.version, null);
            }
        };
        RHDPDownloadsAllItem.prototype.osVersionExtract = function (productId) {
            var osPlatform = new RHDPOSDownload();
            osPlatform.platformType = osPlatform.getUserAgent();
            osPlatform.downloadURL = this.downloadUrl;
            osPlatform.setOSURL(productId);
            osPlatform.setDownloadURLByPlatform();
            this.downloadUrl = osPlatform.downloadURL;
            this.platform = osPlatform.platformType;
        };
        Object.defineProperty(RHDPDownloadsAllItem, "observedAttributes", {
            get: function () {
                return ['name'];
            },
            enumerable: true,
            configurable: true
        });
        RHDPDownloadsAllItem.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
        };
        return RHDPDownloadsAllItem;
    }(HTMLElement));
    window.customElements.define('rhdp-downloads-all-item', RHDPDownloadsAllItem);
    var templateObject_1$1, templateObject_2;

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
    var __makeTemplateObject$2 = (undefined && undefined.__makeTemplateObject) || function (cooked, raw) {
        if (Object.defineProperty) { Object.defineProperty(cooked, "raw", { value: raw }); } else { cooked.raw = raw; }
        return cooked;
    };
    var RHDPDownloadsAll = (function (_super) {
        __extends$2(RHDPDownloadsAll, _super);
        function RHDPDownloadsAll() {
            var _this = _super.call(this) || this;
            _this.template = function (strings, id, heading) {
                return "<div class=\"download-list\">\n                    <div class=\"large-24 category-label\" id=\"" + id + "\">\n                        <h4>" + heading + "</h4>\n                    </div>\n                </div>\n                ";
            };
            return _this;
        }
        Object.defineProperty(RHDPDownloadsAll.prototype, "id", {
            get: function () {
                return this._id;
            },
            set: function (value) {
                if (this.id === value)
                    return;
                this._id = value;
                this.setAttribute('id', this._id);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPDownloadsAll.prototype, "heading", {
            get: function () {
                return this._heading;
            },
            set: function (value) {
                if (this.heading === value)
                    return;
                this._heading = value;
                this.setAttribute('heading', this._heading);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPDownloadsAll.prototype, "products", {
            get: function () {
                return this._products;
            },
            set: function (value) {
                if (this.products === value)
                    return;
                this._products = value;
            },
            enumerable: true,
            configurable: true
        });
        RHDPDownloadsAll.prototype.connectedCallback = function () {
            this.innerHTML = this.template(templateObject_1$2 || (templateObject_1$2 = __makeTemplateObject$2(["", "", ""], ["", "", ""])), this.id, this.heading);
            this.getProductsWithTargetHeading(this.products);
        };
        RHDPDownloadsAll.prototype.getProductsWithTargetHeading = function (productList) {
            if (productList.products) {
                var products = productList.products.products;
                var len = products.length;
                for (var i = 0; i < len; i++) {
                    if (products[i].groupHeading === this.heading) {
                        var item = new RHDPDownloadsAllItem();
                        item.name = products[i].productName;
                        item.productId = products[i].productCode ? products[i].productCode : "";
                        item.dataFallbackUrl = products[i].dataFallbackUrl ? products[i].dataFallbackUrl : "";
                        item.downloadUrl = products[i].downloadLink ? products[i].downloadLink : "";
                        item.description = products[i].description ? products[i].description : "";
                        item.learnMore = products[i].learnMoreLink ? products[i].learnMoreLink : "";
                        item.version = products[i].version ? products[i].version : "";
                        this.querySelector('.download-list').appendChild(item);
                    }
                }
            }
        };
        Object.defineProperty(RHDPDownloadsAll, "observedAttributes", {
            get: function () {
                return ['id', 'heading'];
            },
            enumerable: true,
            configurable: true
        });
        RHDPDownloadsAll.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
        };
        return RHDPDownloadsAll;
    }(HTMLElement));
    window.customElements.define('rhdp-downloads-all', RHDPDownloadsAll);
    var templateObject_1$2;

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
    var __makeTemplateObject$3 = (undefined && undefined.__makeTemplateObject) || function (cooked, raw) {
        if (Object.defineProperty) { Object.defineProperty(cooked, "raw", { value: raw }); } else { cooked.raw = raw; }
        return cooked;
    };
    var RHDPDownloadsPopularProduct = (function (_super) {
        __extends$3(RHDPDownloadsPopularProduct, _super);
        function RHDPDownloadsPopularProduct() {
            var _this = _super.call(this) || this;
            _this.template = function (strings, name, id, dataFallbackUrl, url) {
                return "\n        <div class=\"large-6 column\">\n            <div class=\"popular-download-box\">\n                <h4>" + name + "</h4>\n                <a class=\"pf-c-button pf-m-heavy\" data-download-id=\"" + id + "\" data-fallback-url=\"" + dataFallbackUrl + "\" href=\"" + url + "\"><i class=\"fa fa-download\"></i> Download</a>\n            </div>\n        </div>";
            };
            return _this;
        }
        Object.defineProperty(RHDPDownloadsPopularProduct.prototype, "name", {
            get: function () {
                return this._name;
            },
            set: function (value) {
                if (this._name === value)
                    return;
                this._name = value;
                this.setAttribute('name', this.name);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPDownloadsPopularProduct.prototype, "productId", {
            get: function () {
                return this._productId;
            },
            set: function (value) {
                if (this.productId === value)
                    return;
                this._productId = value;
                this.setAttribute('productid', this.productId);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPDownloadsPopularProduct.prototype, "dataFallbackUrl", {
            get: function () {
                return this._dataFallbackUrl;
            },
            set: function (value) {
                if (this.dataFallbackUrl === value)
                    return;
                this._dataFallbackUrl = value;
                this.setAttribute('datafallbackurl', this.dataFallbackUrl);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPDownloadsPopularProduct.prototype, "downloadUrl", {
            get: function () {
                return this._downloadUrl;
            },
            set: function (value) {
                if (this.downloadUrl === value)
                    return;
                this._downloadUrl = value;
                this.setAttribute('downloadurl', this.downloadUrl);
            },
            enumerable: true,
            configurable: true
        });
        RHDPDownloadsPopularProduct.prototype.osVersionExtract = function (productId) {
            var osPlatform = new RHDPOSDownload();
            osPlatform.platformType = osPlatform.getUserAgent();
            osPlatform.downloadURL = this.downloadUrl;
            osPlatform.setOSURL(productId);
            osPlatform.setDownloadURLByPlatform();
            this.downloadUrl = osPlatform.downloadURL;
        };
        RHDPDownloadsPopularProduct.prototype.connectedCallback = function () {
            this.osVersionExtract(this.productId);
            this.innerHTML = this.template(templateObject_1$3 || (templateObject_1$3 = __makeTemplateObject$3(["", "", "", "", ""], ["", "", "", "", ""])), this.name, this.productId, this.dataFallbackUrl, this.downloadUrl);
        };
        Object.defineProperty(RHDPDownloadsPopularProduct, "observedAttributes", {
            get: function () {
                return ['name', 'productid', 'downloadurl', 'datafallbackurl'];
            },
            enumerable: true,
            configurable: true
        });
        RHDPDownloadsPopularProduct.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
        };
        return RHDPDownloadsPopularProduct;
    }(HTMLElement));
    window.customElements.define('rhdp-downloads-popular-product', RHDPDownloadsPopularProduct);
    var templateObject_1$3;

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
    var RHDPDownloadsPopularProducts = (function (_super) {
        __extends$4(RHDPDownloadsPopularProducts, _super);
        function RHDPDownloadsPopularProducts() {
            return _super.call(this) || this;
        }
        Object.defineProperty(RHDPDownloadsPopularProducts.prototype, "productList", {
            get: function () {
                return this._productList;
            },
            set: function (value) {
                if (this._productList === value)
                    return;
                this._productList = value;
            },
            enumerable: true,
            configurable: true
        });
        RHDPDownloadsPopularProducts.prototype.addProduct = function (product) {
            var productNode = new RHDPDownloadsPopularProduct();
            productNode.name = product.productName;
            productNode.productId = product.productCode;
            productNode.dataFallbackUrl = product.dataFallbackUrl;
            productNode.downloadUrl = product.downloadLink;
            this.appendChild(productNode);
        };
        RHDPDownloadsPopularProducts.prototype.renderProductList = function () {
            if (this.productList.products) {
                var products = this.productList.products;
                var len = products.length;
                for (var i = 0; i < len; i++) {
                    if (products[i].featured) {
                        this.addProduct(products[i]);
                    }
                }
            }
        };
        RHDPDownloadsPopularProducts.prototype.connectedCallback = function () {
            this.renderProductList();
        };
        RHDPDownloadsPopularProducts.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
        };
        return RHDPDownloadsPopularProducts;
    }(HTMLElement));
    window.customElements.define('rhdp-downloads-popular-products', RHDPDownloadsPopularProducts);

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
    var RHDPDownloadsProducts = (function (_super) {
        __extends$5(RHDPDownloadsProducts, _super);
        function RHDPDownloadsProducts() {
            var _this = _super.call(this) || this;
            _this._products = {
                "products": [{
                        "productName": "Red Hat JBoss Data Grid",
                        "groupHeading": "ACCELERATED DEVELOPMENT AND MANAGEMENT",
                        "productCode": "datagrid",
                        "featured": false,
                        "dataFallbackUrl": "https://access.redhat.com/jbossnetwork/restricted/listSoftware.html?product=data.grid&downloadType=distributions",
                        "downloadLink": "",
                        "description": "An in-memory data grid to accelerate performance that is fast, distributed, scalable, and independent from the data tier.",
                        "version": "",
                        "learnMoreLink": "/products/datagrid/overview/"
                    }, {
                        "productName": "Red Hat JBoss Enterprise Application Platform",
                        "groupHeading": "ACCELERATED DEVELOPMENT AND MANAGEMENT",
                        "productCode": "eap",
                        "featured": false,
                        "dataFallbackUrl": "https://access.redhat.com/jbossnetwork/restricted/listSoftware.html?product=appplatform&downloadType=distributions",
                        "downloadLink": "",
                        "description": "An innovative, modular, cloud-ready application platform that addresses management, automation and developer productivity.",
                        "version": "",
                        "learnMoreLink": "/products/eap/overview/"
                    }, {
                        "productName": "Red Hat JBoss Web Server",
                        "groupHeading": "ACCELERATED DEVELOPMENT AND MANAGEMENT",
                        "featured": false,
                        "dataFallbackUrl": "https://access.redhat.com/jbossnetwork/restricted/listSoftware.html?downloadType=distributions&product=webserver&productChanged=yes",
                        "downloadLink": "/products/webserver/download/",
                        "description": "Apache httpd, Tomcat, etc. to provide a single solution for large-scale websites and light-weight Java web applications.",
                        "version": "",
                        "learnMoreLink": "/products/webserver/overview/"
                    }, {
                        "productName": "Red Hat Application Migration Toolkit",
                        "groupHeading": "DEVELOPER TOOLS",
                        "productCode": "migrationtoolkit",
                        "featured": false,
                        "dataFallbackUrl": "https://access.redhat.com/downloads",
                        "downloadLink": "",
                        "description": "Red Hat Application Migration Toolkit is an assembly of open source tools that enables large-scale application migrations and modernizations. The tooling consists of multiple individual components that provide support for each phase of a migration process.",
                        "version": "",
                        "learnMoreLink": "/products/rhamt/overview/"
                    }, {
                        "productName": "Red Hat Container Development Kit",
                        "groupHeading": "DEVELOPER TOOLS",
                        "productCode": "cdk",
                        "featured": false,
                        "dataFallbackUrl": "https://access.redhat.com/downloads/content/293/",
                        "downloadLink": "",
                        "description": "For container development, includes RHEL and OpenShift 3.",
                        "version": "",
                        "learnMoreLink": "/products/cdk/overview/"
                    }, {
                        "productName": "Red Hat JBoss Developer Studio",
                        "groupHeading": "DEVELOPER TOOLS",
                        "productCode": "devstudio",
                        "featured": false,
                        "dataFallbackUrl": "https://access.redhat.com/jbossnetwork/restricted/listSoftware.html?product=jbossdeveloperstudio&downloadType=distributions",
                        "downloadLink": "",
                        "description": "An Eclipse-based IDE to create apps for web, mobile, transactional enterprise, and SOA-based integration apps/services.",
                        "version": "",
                        "learnMoreLink": "/products/devstudio/overview/"
                    }, {
                        "productName": "Red Hat Enterprise Linux",
                        "groupHeading": "INFRASTRUCTURE",
                        "productCode": "rhel",
                        "featured": true,
                        "dataFallbackUrl": "https://access.redhat.com/downloads/content/69/",
                        "downloadLink": "",
                        "description": "For traditional development, includes Software Collections and Developer Toolset.",
                        "version": "",
                        "learnMoreLink": "/products/rhel/overview/"
                    }, {
                        "productName": "Red Hat JBoss AMQ",
                        "groupHeading": "INTEGRATION AND AUTOMATION",
                        "productCode": "amq",
                        "featured": false,
                        "dataFallbackUrl": "https://access.redhat.com/jbossnetwork/restricted/listSoftware.html?product=jboss.amq&downloadType=distributions",
                        "downloadLink": "",
                        "description": "A small-footprint, performant, robust messaging platform that enables real-time app, device, and service integration.",
                        "version": "",
                        "learnMoreLink": "/products/amq/overview/"
                    }, {
                        "productName": "Red Hat Decision Manager",
                        "groupHeading": "INTEGRATION AND AUTOMATION",
                        "productCode": "brms",
                        "featured": false,
                        "dataFallbackUrl": "https://access.redhat.com/jbossnetwork/restricted/listSoftware.html?product=brms&downloadType=distributions",
                        "downloadLink": "",
                        "description": "A programming platform to easily capture and maintain rules for business changes, without impacting static applications.",
                        "version": "",
                        "learnMoreLink": "/products/red-hat-decision-manager/overview/"
                    }, {
                        "productName": "Red Hat Process Automation Manager",
                        "groupHeading": "INTEGRATION AND AUTOMATION",
                        "productCode": "rhpam",
                        "featured": false,
                        "dataFallbackUrl": "https://access.redhat.com/jbossnetwork/restricted/listSoftware.html?downloadType=distributions&product=bpm.suite&productChanged=yes",
                        "downloadLink": "",
                        "description": "A platform that combines business rules and process management (BPM), and complex event processing.",
                        "version": "",
                        "learnMoreLink": "/products/rhpam/overview/"
                    }, {
                        "productName": "Red Hat JBoss Data Virtualization",
                        "groupHeading": "INTEGRATION AND AUTOMATION",
                        "productCode": "datavirt",
                        "featured": false,
                        "dataFallbackUrl": "https://access.redhat.com/jbossnetwork/restricted/listSoftware.html?product=data.services.platform&downloadType=distributions",
                        "downloadLink": "",
                        "description": "A tool that brings operational and analytical insight from data dispersed in various business units, apps, and technologies.",
                        "version": "",
                        "learnMoreLink": "/products/datavirt/overview/"
                    }, {
                        "productName": "Red Hat JBoss Fuse",
                        "groupHeading": "INTEGRATION AND AUTOMATION",
                        "productCode": "fuse",
                        "featured": true,
                        "dataFallbackUrl": "https://access.redhat.com/jbossnetwork/restricted/listSoftware.html?product=jboss.fuse&downloadType=distributions",
                        "downloadLink": "",
                        "description": "A small-footprint enterprise service bus (ESB) that lets you build, deploy and integrate applications and services.",
                        "version": "",
                        "learnMoreLink": "/products/fuse/overview/"
                    }, {
                        "productName": "Red Hat Mobile Application Platform",
                        "groupHeading": "MOBILE",
                        "featured": true,
                        "dataFallbackUrl": "https://access.redhat.com/downloads/content/316/",
                        "downloadLink": "/products/mobileplatform/download/",
                        "description": "Develop and deploy mobile apps in an agile and flexible manner.",
                        "version": "",
                        "learnMoreLink": "/products/mobileplatform/overview/"
                    }, {
                        "productName": "Red Hat OpenShift Container Platform",
                        "groupHeading": "CLOUD",
                        "productCode": "openshift",
                        "featured": false,
                        "dataFallbackUrl": "https://access.redhat.com/downloads/content/290/",
                        "downloadLink": "",
                        "description": "An open, hybrid Platform-as-a-Service (PaaS) to quickly develop, host, scale, and deliver apps in the cloud.",
                        "version": "",
                        "learnMoreLink": "/products/openshift/overview/"
                    }, {
                        "productName": "OpenJDK",
                        "groupHeading": "LANGUAGES AND COMPILERS",
                        "productCode": "openjdk",
                        "featured": false,
                        "dataFallbackUrl": "/products/openjdk/overview/",
                        "downloadLink": "",
                        "description": "A Tried, Tested and Trusted open source implementation of the Java platform",
                        "version": "",
                        "learnMoreLink": "/products/openjdk/overview/"
                    }]
            };
            return _this;
        }
        Object.defineProperty(RHDPDownloadsProducts.prototype, "category", {
            get: function () {
                return this._category;
            },
            set: function (value) {
                if (this._category === value)
                    return;
                this._category = value;
                this.setAttribute('category', this._category);
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPDownloadsProducts.prototype, "products", {
            get: function () {
                return this._products;
            },
            set: function (value) {
                if (this._products === value)
                    return;
                this._products = value;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(RHDPDownloadsProducts.prototype, "data", {
            get: function () {
                return this._data;
            },
            set: function (value) {
                if (this._data === value)
                    return;
                this._data = value;
                this.setAttribute('data', this._data);
                this._createProductList();
            },
            enumerable: true,
            configurable: true
        });
        RHDPDownloadsProducts.prototype._createProductList = function () {
            var tempProductList = { "products": [] };
            if (this._data) {
                var productLen = this.products.products.length;
                var dataLen = this.data.length;
                for (var i = 0; i < productLen; i++) {
                    var product = this.products.products[i];
                    for (var j = 0; j < dataLen; j++) {
                        var data = this.data[j];
                        if (data['productCode'] == product['productCode']) {
                            this.products.products[i]['downloadLink'] = data['featuredArtifact']['url'];
                            this.products.products[i]['version'] = data['featuredArtifact']['versionName'];
                        }
                    }
                    tempProductList['products'].push(product);
                }
            }
            this.products = tempProductList;
        };
        return RHDPDownloadsProducts;
    }(HTMLElement));
    window.customElements.define('rhdp-downloads-products', RHDPDownloadsProducts);

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
    var RHDPDownloadsApp = (function (_super) {
        __extends$6(RHDPDownloadsApp, _super);
        function RHDPDownloadsApp() {
            var _this = _super.call(this) || this;
            _this.stage_download_url = 'https://developers.stage.redhat.com';
            _this.popularProduct = new RHDPDownloadsPopularProducts();
            _this.products = new RHDPDownloadsProducts();
            _this.template = "<div class=\"hero hero-wide hero-downloads\">\n                    <div class=\"row\">\n                        <div class=\"large-12 medium-24 columns\" id=\"downloads\">\n                            <h2>Downloads</h2>\n                        </div>\n                    </div>\n                </div>\n                <span class=\"dl-outage-msg\"></span>\n                <div class=\"most-popular-downloads\">\n                    <div class=\"row\">\n                        <div class=\"large-24 column\">\n                            <h3>Most Popular</h3>\n                        </div>\n                    </div>\n                \n                    <div class=\"row\">\n                    </div>\n                </div>\n                <div class=\"row\" id=\"downloads\">\n                    <div class=\"large-24 columns\">\n                        <h3 class=\"downloads-header\">All Downloads</h3>\n                    </div>\n                </div>";
            return _this;
        }
        Object.defineProperty(RHDPDownloadsApp.prototype, "url", {
            get: function () {
                return this._url;
            },
            set: function (val) {
                if (this._url === val)
                    return;
                this._url = val;
                this.setAttribute('url', this.url);
            },
            enumerable: true,
            configurable: true
        });
        RHDPDownloadsApp.prototype.connectedCallback = function () {
            this.innerHTML = this.template;
            this.setProductsDownloadData(this.url);
        };
        RHDPDownloadsApp.prototype.addGroups = function (productList) {
            this.querySelector('#downloads .large-24').appendChild(this.downloadsAllFactory('accelerated_development_and_management', 'ACCELERATED DEVELOPMENT AND MANAGEMENT', productList));
            this.querySelector('#downloads .large-24').appendChild(this.downloadsAllFactory('developer_tools', 'DEVELOPER TOOLS', productList));
            this.querySelector('#downloads .large-24').appendChild(this.downloadsAllFactory('infrastructure', 'INFRASTRUCTURE', productList));
            this.querySelector('#downloads .large-24').appendChild(this.downloadsAllFactory('integration_and_automation', 'INTEGRATION AND AUTOMATION', productList));
            this.querySelector('#downloads .large-24').appendChild(this.downloadsAllFactory('mobile', 'MOBILE', productList));
            this.querySelector('#downloads .large-24').appendChild(this.downloadsAllFactory('cloud', 'CLOUD', productList));
            this.querySelector('#downloads .large-24').appendChild(this.downloadsAllFactory('runtimes', 'LANGUAGES AND COMPILERS', productList));
        };
        RHDPDownloadsApp.prototype.setPopularProducts = function (productList) {
            this.popularProduct.productList = productList.products;
            this.querySelector('.most-popular-downloads .row').appendChild(this.popularProduct);
        };
        RHDPDownloadsApp.prototype.downloadsAllFactory = function (id, heading, productList) {
            var downloads = new RHDPDownloadsAll();
            downloads.id = id;
            downloads.heading = heading;
            downloads.products = productList;
            return downloads;
        };
        RHDPDownloadsApp.prototype.setProductsDownloadData = function (url) {
            var _this = this;
            if (window.location.origin.indexOf('developers.stage.redhat.com') > 0) {
                url = url.replace(/http(s)?:\/\/developers.redhat.com/g, this.stage_download_url);
            }
            var fInit = {
                method: 'GET',
                headers: new Headers(),
                mode: 'cors',
                cache: 'default'
            };
            fetch(url, fInit)
                .then(function (resp) { return resp.json(); })
                .then(function (data) {
                _this.products.data = data;
                _this.setPopularProducts(_this.products);
                _this.addGroups(_this.products);
            });
        };
        Object.defineProperty(RHDPDownloadsApp, "observedAttributes", {
            get: function () {
                return ['url'];
            },
            enumerable: true,
            configurable: true
        });
        RHDPDownloadsApp.prototype.attributeChangedCallback = function (name, oldVal, newVal) {
            this[name] = newVal;
        };
        return RHDPDownloadsApp;
    }(HTMLElement));
    window.customElements.define('rhdp-downloads-app', RHDPDownloadsApp);

    new RHDPOSDownload();
    new RHDPDownloadsAllItem();
    new RHDPDownloadsAll();
    new RHDPDownloadsApp();
    new RHDPDownloadsPopularProduct();
    new RHDPDownloadsPopularProducts();
    new RHDPDownloadsProducts();

})));
