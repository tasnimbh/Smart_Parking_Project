import VanillaRouter from "./router.js";

const router = new VanillaRouter({
    type: "history",
    routes: {
        "/": "home",
        "/about": "about",
        "/products": "products"
    }
}).listen().on("route", async e => {

    console.log(e.detail.route, e.detail.url);

    document.querySelector("main").innerHTML = await fetch("/pages/" + e.detail.route + ".html").then(x=>x.text())

})