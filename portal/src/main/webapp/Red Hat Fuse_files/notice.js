function _truste_eumap(){truste=self.truste||{};truste.eu||(truste.eu={});var f=truste.eu.bindMap={version:"v1.7-14",domain:"redhat.com",width:parseInt("829"),height:parseInt("460"),baseName:"te-notice-clr1-7c36c840-7dd6-4005-a474-d3c98e14ce1e",showOverlay:"{ShowLink}",hideOverlay:"{HideLink}",anchName:"te-notice-clr1-7c36c840-7dd6-4005-a474-d3c98e14ce1e-anch",intDivName:"te-notice-clr1-7c36c840-7dd6-4005-a474-d3c98e14ce1e-itl",iconSpanId:"te-notice-clr1-7c36c840-7dd6-4005-a474-d3c98e14ce1e-icon",containerId:(!"teconsent"||/^_LB.*LB_$/.test("teconsent"))?"teconsent":"teconsent",messageBaseUrl:"http://consent.trustarc.com/noticemsg?",daxSignature:"",privacyUrl:"",prefmgrUrl:"http://consent-pref.trustarc.com/?type=redhatslider",text:"true",icon:"Cookie Preferences",locale:"en",language:"en",country:"de",state:"",noticeJsURL:((parseInt("0")?"https://consent-st.trustarc.com/":"http://consent.trustarc.com/"))+"asset/notice.js/v/v1.7-14",assetServerURL:(parseInt("0")?"https://consent-st.trustarc.com/":"http://consent.trustarc.com/")+"asset/",cdnURL:"https://consent-st.trustarc.com/".replace(/^(http:)?\/\//,"https://"),iconBaseUrl:"http://consent.trustarc.com/",behavior:"expressed",behaviorManager:"eu",provisionedFeatures:"",cookiePreferenceIcon:"trustarc_cookiepreferences.png",cookieExpiry:parseInt("395",10)||395,closeButtonUrl:"https://consent.trustarc.com/get?name=redhat_close.svg",apiDefaults:'{"reportlevel":16777215}',cmTimeout:parseInt("6000",10),popTime:new Date("".replace(" +0000","Z").replace(" ","T")).getTime()||null,popupMsg:"",bannerMsgURL:"http://consent.trustarc.com/bannermsg?",IRMIntegrationURL:"",irmWidth:parseInt(""),irmHeight:parseInt(""),irmContainerId:(!"_LBirmcLB_"||/^_LB.*LB_$/.test("_LBirmcLB_"))?"teconsent":"_LBirmcLB_",irmText:"",lspa:"",ccpaText:"",feat:{iabGdprApplies:true,consentResolution:false,dropBehaviorCookie:true,crossDomain:false,uidEnabled:false,replaceDelimiter:false,enableBanner:true,enableIRM:false,enableCM:true,enableCCPA:false,ccpaApplies:false,enableCM:true,unprovisionedDropBehavior:false,unprovisionedIab:false,unprovisionedCCPA:false},autoDisplayCloseButton:false};
if(/layout=gdpr/.test(f.prefmgrUrl)){f.isGdprLayout=true}if(/layout=iab/.test(f.prefmgrUrl)){f.isIabLayout=true
}if(self.location.protocol!="http:"){for(var a in f){if(f[a]&&f[a].replace){f[a]=f[a].replace(/^(http:)?\/\//,"https://")
}}}if(!truste.cma){var c=self.document,b=c.createElement("script");b.setAttribute("async","async");b.setAttribute("type","text/javascript");
b.setAttribute("crossorigin","");b.src=f.noticeJsURL;(c.getElementById(f.containerId)||c.getElementsByTagName("body")[0]||c.getElementsByTagName("head")[0]).appendChild(b)
}(function(e){if(e.feat.crossDomain){var d=function(){if(!window.frames.trustarc_notice){if(document.body){var g=document.body,h=document.createElement("iframe");
h.style.display="none";h.name="trustarc_notice";h.id="trustarcNoticeFrame";h.src=e.cdnURL+"get?name=crossdomain.html&domain="+e.domain;
g.appendChild(h)}else{setTimeout(d,5)}}};d()}})(truste.eu.bindMap);(function(k){const e="__cmpTrustarc";
const j="__tcfapiTrustarc";var i=function(){if(k.feat.iab){return}var n=self.document,m=n.createElement("script");
m.setAttribute("async","async");m.setAttribute("type","text/javascript");m.setAttribute("crossorigin","");
m.src="//trustarc.mgr.consensu.org/get?name=cmp.js";(n.getElementById(k.containerId)||n.getElementsByTagName("body")[0]||n.getElementsByTagName("head")[0]).appendChild(m);
k.feat.iab=true};var l=function(){if(k.feat.iab){return}var n=self.document,m=n.createElement("script");
m.setAttribute("async","async");m.setAttribute("type","text/javascript");m.setAttribute("crossorigin","");
m.src="https://trustarc.mgr.consensu.org/asset/tcfapi.js";(n.getElementById(k.containerId)||n.getElementsByTagName("body")[0]||n.getElementsByTagName("head")[0]).appendChild(m);
k.feat.iab=true};if(document.getElementById(e)){i()}else{var d=new MutationObserver(function g(m){if(document.getElementById(e)){d.disconnect();
i()}});d.observe(document.body||document.getElementsByTagName("body")[0]||document.documentElement,{attributes:false,childList:true,characterData:false,subtree:true});
setTimeout(function(){d.disconnect()},30000)}if(document.getElementById(j)){l()}else{var h=new MutationObserver(function g(m){if(document.getElementById(j)){h.disconnect();
l()}});h.observe(document.body||document.getElementsByTagName("body")[0]||document.documentElement,{attributes:false,childList:true,characterData:false,subtree:true});
setTimeout(function(){h.disconnect()},30000)}})(truste.eu.bindMap);$temp_box_overlay={padding:"0px","border-radius":"3px"};
$temp_closebtn_style={top:"16px",right:"14px"};$temp_inner_iframe={"border-radius":"3px"};f.styles={};
f.styles.closebtn=typeof $temp_closebtn_style!="undefined"&&$temp_closebtn_style;f.styles.box_overlay=typeof $temp_box_overlay!="undefined"&&$temp_box_overlay;
f.styles.overlay=typeof $temp_overlay!="undefined"&&$temp_overlay;f.styles.inner_iframe=typeof $temp_inner_iframe!="undefined"&&$temp_inner_iframe;
f.styles.outerdiv=typeof $temp_style_outerdiv!="undefined"&&$temp_style_outerdiv;f.outerdiv=typeof $temp_outerdiv!="undefined";
truste.eu.ccpa={};truste.eu.COOKIE_USPRIVACY="usprivacy";truste.eu.USP_VERSION="1";truste.eu.USP_FILE_NAME="uspapi.js";
truste.eu.ccpa.uspString=function(i){var d=truste.eu.bindMap;var h="1---";if(d.feat.ccpaApplies){var g=/^[nNyY-]$/;
var e=(g.test(d.lspa))?d.lspa:"-";i=(i)?"Y":"N";h=truste.eu.USP_VERSION+"Y"+i+e}return h};truste.eu.ccpa.dropCcpaCookie=function(e){var d=truste.eu.bindMap;
if(d.feat.enableCCPA){truste.util.createCookie(truste.eu.COOKIE_USPRIVACY,truste.eu.ccpa.uspString(e))
}};truste.eu.ccpa.getOptout=function(){var d=/^[1][nNyY-][nNyY-][nNyY-]$/;var e=truste.util.readCookie(truste.eu.COOKIE_USPRIVACY);
if(d.test(e)){return e.charAt(2)}return null};truste.eu.ccpa.showLink=function(){var d=truste.eu.bindMap;
var g=truste.eu.ccpaLink=self.document.createElement("a");truste.util.addListener(g,"click",function h(){truste.bn.reopenBanner&&truste.bn.reopenBanner()
});g.id=truste.eu.irmId=("ccpa-id-"+Math.random()).replace(".","");g.tabIndex="0";g.setAttribute("role","link");
g.setAttribute("lang",d.language);truste.util.addListener(g,"keydown",function(i){if(!i){i=window.event
}var j=i.which||i.keyCode;if(j==13||j==32){i.preventDefault?i.preventDefault():event.returnValue=false;
g.click()}});g.style.cursor="pointer";g.innerHTML=d.ccpaText;var e=document.getElementById(d.containerId);
if(e){e.appendChild(g);e.style.display=truste.util.getDisplayProperty(e)}};truste.eu.ccpa.initialize=function(){var d=truste.eu.bindMap;
if(d.feat.enableCCPA){truste.util.addScriptElement(d.assetServerURL+truste.eu.USP_FILE_NAME,function e(){if(!d.prefCookie){truste.eu.ccpa.dropCcpaCookie(false)
}if(d.feat.ccpaApplies){var g=setInterval(function(){var h=self.document.getElementById(d.containerId);
if(h){clearInterval(g);truste.eu.ccpa.showLink()}},100)}setTimeout(function(){clearInterval(g)},10000)
})}}}self._truste&&(self._truste.eumap=_truste_eumap)||_truste_eumap();