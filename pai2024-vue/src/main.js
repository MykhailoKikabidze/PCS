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
import Charts from "./components/Charts.vue";
import Analysis from "./components/Analysis.vue";
import Chat from "./components/Chat.vue";
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
    },
    {
      path: "/projects",
      component: ProjectList,
      title: "Projekty",
      icon: "mdi-projector",
    },
    {
      path: "/charts",
      component: Charts,
      title: "Wykresy",
      icon: "mdi-chart-bar",
    },
    {
      path: "/chat",
      component: Chat,
      title: "Czat",
      icon: "mdi-chat-outline",
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
