if(!self.define){let e,n={};const i=(i,l)=>(i=new URL(i+".js",l).href,n[i]||new Promise((n=>{if("document"in self){const e=document.createElement("script");e.src=i,e.onload=n,document.head.appendChild(e)}else e=i,importScripts(i),n()})).then((()=>{let e=n[i];if(!e)throw new Error(`Module ${i} didn’t register its module`);return e})));self.define=(l,s)=>{const r=e||("document"in self?document.currentScript.src:"")||location.href;if(n[r])return;let o={};const u=e=>i(e,r),c={module:{uri:r},exports:o,require:u};n[r]=Promise.all(l.map((e=>c[e]||u(e)))).then((e=>(s(...e),o)))}}define(["./workbox-5b385ed2"],(function(e){"use strict";e.setCacheNameDetails({prefix:"ttms_webapp"}),self.addEventListener("message",(e=>{e.data&&"SKIP_WAITING"===e.data.type&&self.skipWaiting()})),e.precacheAndRoute([{url:"/css/app.af2162a9.css",revision:null},{url:"/css/chunk-vendors.ab49d789.css",revision:null},{url:"/fonts/element-icons.f1a45d74.ttf",revision:null},{url:"/fonts/element-icons.ff18efd1.woff",revision:null},{url:"/img/about1.8b1b9ced.png",revision:null},{url:"/img/carousel1.7a4f062b.jpg",revision:null},{url:"/img/carousel2.da601ce0.png",revision:null},{url:"/img/carousel3.e1f48a55.jpg",revision:null},{url:"/img/carousel4.2937ea28.png",revision:null},{url:"/img/cinema.a8c39ac4.png",revision:null},{url:"/img/code.2036f780.png",revision:null},{url:"/img/head_sculpture.7d3355e1.jpg",revision:null},{url:"/img/tianmu.0c4902a2.jpg",revision:null},{url:"/index.html",revision:"5a6cd9c74c18d3f19f3e9eb15d441f68"},{url:"/js/app.181aa237.js",revision:null},{url:"/js/chunk-vendors.2432c0e1.js",revision:null},{url:"/manifest.json",revision:"598c68ab3feef057ec162da74a7318a8"},{url:"/robots.txt",revision:"b6216d61c03e6ce0c9aea6ca7808f7ca"}],{})}));
//# sourceMappingURL=service-worker.js.map
