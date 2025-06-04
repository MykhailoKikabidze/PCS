import "./assets/main.css";
import "@mdi/font/css/materialdesignicons.css";

import { createApp } from "vue";

// Vuetify
import "vuetify/styles";
import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import * as directives from "vuetify/directives";

import App from "./App.vue";

const vuetify = createVuetify({
  components,
  directives,
  icons: {
    defaultSet: "mdi",
  },
});

// Router
import { createRouter, createWebHashHistory } from "vue-router";
import Dashboard from "./components/Dashboard.vue";
import ProjectList from "./components/ProjectList.vue";
import Analysis from "./components/Analysis.vue";
import Settings from "./components/Settings.vue";

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: "/",
      component: Dashboard,
      title: "Pulpit",
      icon: "mdi-home",
      public: true,
      props: true,
    },
    {
      path: "/projects",
      component: ProjectList,
      title: "Projekty",
      icon: "mdi-projector",
    },
    {
      path: "/analysis",
      component: Analysis,
      title: "Analiza",
      icon: "mdi-chart-bar",
    },
    {
      path: "/settings",
      component: Settings,
      title: "Ustawienia",
      icon: "mdi-cog",
    },
  ],
});

import ganttastic from "@infectoone/vue-ganttastic";

createApp(App).use(vuetify).use(ganttastic).use(router).mount("#app");
