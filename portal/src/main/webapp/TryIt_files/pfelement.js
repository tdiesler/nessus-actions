!function(e,t){"object"==typeof exports&&"undefined"!=typeof module?module.exports=t():"function"==typeof define&&define.amd?define(t):e.PFElement=t()}(this,function(){"use strict";var e="function"==typeof Symbol&&"symbol"==typeof Symbol.iterator?function(e){return typeof e}:function(e){return e&&"function"==typeof Symbol&&e.constructor===Symbol&&e!==Symbol.prototype?"symbol":typeof e},t=function(e,t,n){return t&&o(e.prototype,t),n&&o(e,n),e};function o(e,t){for(var n=0;n<t.length;n++){var o=t[n];o.enumerable=o.enumerable||!1,o.configurable=!0,"value"in o&&(o.writable=!0),Object.defineProperty(e,o.key,o)}}function c(e){if(Array.isArray(e)){for(var t=0,n=Array(e.length);t<e.length;t++)n[t]=e[t];return n}return Array.from(e)}var r=function(){return null};function i(){r("[reveal] web components ready"),r("[reveal] elements ready, revealing the body"),window.document.body.removeAttribute("unresolved")}var l="pfe-",n=(function(e,t){if("function"!=typeof t&&null!==t)throw new TypeError("Super expression must either be null or a function, not "+typeof t);e.prototype=Object.create(t&&t.prototype,{constructor:{value:e,enumerable:!1,writable:!0,configurable:!0}}),t&&(Object.setPrototypeOf?Object.setPrototypeOf(e,t):e.__proto__=t)}(u,HTMLElement),t(u,[{key:"has_slot",value:function(e){return this.querySelector("[slot='"+e+"']")}},{key:"has_slots",value:function(e){return[].concat(c(this.querySelectorAll("[slot='"+e+"']")))}},{key:"version",get:function(){return this._pfeClass.version}},{key:"pfeType",get:function(){return this.getAttribute(l+"type")},set:function(e){this.setAttribute(l+"type",e)}}],[{key:"create",value:function(e){window.customElements.define(e.tag,e)}},{key:"debugLog",value:function(e){var t=0<arguments.length&&void 0!==e?e:null;return null!==t&&(u._debugLog=!!t),u._debugLog}},{key:"log",value:function(){var e;u.debugLog()&&(e=console).log.apply(e,arguments)}},{key:"PfeTypes",get:function(){return{Container:"container",Content:"content",Combo:"combo"}}},{key:"version",get:function(){return"1.0.0-prerelease.24"}},{key:"randomId",get:function(){return Math.random().toString(36).substr(2,9)}}]),t(u,[{key:"connectedCallback",value:function(){this.connected=!0,window.ShadyCSS&&window.ShadyCSS.styleElement(this),this.classList.add("PFElement"),this.setAttribute("pfelement",""),"object"===e(this.props)&&this._mapSchemaToProperties(this.tag,this.props),"object"===e(this.slots)&&this._mapSchemaToSlots(this.tag,this.slots),this._queue.length&&this._processQueue()}},{key:"disconnectedCallback",value:function(){this.connected=!1}},{key:"attributeChangedCallback",value:function(e,t,n){if(this._pfeClass.cascadingAttributes){var o=this._pfeClass.cascadingAttributes[e];o&&this._copyAttribute(e,o)}}},{key:"_copyAttribute",value:function(e,t){var n=[].concat(c(this.querySelectorAll(t)),c(this.shadowRoot.querySelectorAll(t))),o=this.getAttribute(e),r=null==o?"removeAttribute":"setAttribute",i=!0,a=!1,u=void 0;try{for(var s,l=n[Symbol.iterator]();!(i=(s=l.next()).done);i=!0)s.value[r](e,o)}catch(e){a=!0,u=e}finally{try{!i&&l.return&&l.return()}finally{if(a)throw u}}}},{key:"_mapSchemaToProperties",value:function(a,u){var s=this;Object.keys(u).forEach(function(e){var t=u[e],n=!0,o=e;if(s[e]=t,s[e].value=null,void 0!==s[e].prefixed&&(n=s[e].prefixed),n&&(o=l+e),s.hasAttribute(o))s[e].value=s.getAttribute(o);else if(t.default){var r=s._hasDependency(a,t.options),i=!t.options||t.options&&!t.options.dependencies.length;(r||i)&&(s.setAttribute(o,t.default),s[e].value=t.default)}})}},{key:"_hasDependency",value:function(e,t){for(var n=t?t.dependencies:[],o=!1,r=0;r<n.length;r+=1){var i="slot"===n[r].type&&0<this.has_slots(e+"--"+n[r].id).length,a="attribute"===n[r].type&&this.getAttribute(l+n[r].id);if(i||a){o=!0;break}}return o}},{key:"_mapSchemaToSlots",value:function(n,o){var r=this;Object.keys(o).forEach(function(e){var t=!1;o[e].namedSlot?0<r.has_slots(n+"--"+e).length&&(t=!0):0<[].concat(c(r.querySelectorAll(":not([slot])"))).length&&(t=!0),t?r.setAttribute("has_"+e,""):r.removeAttribute("has_"+e)})}},{key:"_queueAction",value:function(e){this._queue.push(e)}},{key:"_processQueue",value:function(){var t=this;this._queue.forEach(function(e){t["_"+e.type](e.data)}),this._queue=[]}},{key:"_setProperty",value:function(e){var t=e.name,n=e.value;this[t]=n}},{key:"var",value:function(e){return u.var(e,this)}},{key:"render",value:function(){this.shadowRoot.innerHTML="",this.template.innerHTML=this.html,window.ShadyCSS&&window.ShadyCSS.prepareTemplate(this.template,this.tag),this.shadowRoot.appendChild(this.template.content.cloneNode(!0))}},{key:"log",value:function(){for(var e=arguments.length,t=Array(e),n=0;n<e;n++)t[n]=arguments[n];u.log.apply(u,["["+this.tag+"]"].concat(t))}}],[{key:"var",value:function(e,t){var n=1<arguments.length&&void 0!==t?t:document.body;return window.getComputedStyle(n).getPropertyValue(e).trim()}}]),u);function u(e){var t=1<arguments.length&&void 0!==arguments[1]?arguments[1]:{},n=t.type,o=void 0===n?null:n,r=t.delayRender,i=void 0!==r&&r;!function(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}(this,u);var a=function(e,t){if(!e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return!t||"object"!=typeof t&&"function"!=typeof t?e:t}(this,(u.__proto__||Object.getPrototypeOf(u)).call(this));return a.connected=!1,a._pfeClass=e,a.tag=e.tag,a.props=e.properties,a.slots=e.slots,a._queue=[],a.template=document.createElement("template"),a.attachShadow({mode:"open"}),o&&a._queueAction({type:"setProperty",data:{name:"pfeType",value:o}}),i||a.render(),a}return function(e){r=e;var t=window.WebComponents,n=t&&window.WebComponents.ready;!t||n?i():window.addEventListener("WebComponentsReady",i)}(n.log),n});
//# sourceMappingURL=pfelement.umd.min.js.map
