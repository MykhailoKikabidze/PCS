<script>
const projectEndpoint = "/api/projects";
const taskEndpoint = "/api/tasks";

const today = new Date();
const todayDate = today.toISOString().substring(0, 10);

const toISOStringDate = (dateNum) =>
  new Date(dateNum).toISOString().substring(0, 10);

const to2Args = (fn) => (arg1, arg2) => fn(arg1, arg2);

const findDate = (arr, getProp, compare) => {
  if (!arr.length) return todayDate;
  const parsed = arr.map(getProp).map(Date.parse);
  return toISOStringDate(parsed.reduce(to2Args(compare), parsed[0]));
};

const extractStartDate = (entity) => entity.startDate;
const extractEndDate = (entity) => entity.dueDate || todayDate;

const getStandardGanttData = (entities) => ({
  start: findDate(entities, extractStartDate, Math.min) + " 00:00",
  end: findDate(entities, extractEndDate, Math.max) + " 23:59",
  entities: entities.map((entity) => ({
    label: entity.name,
    bars: [
      {
        start: extractStartDate(entity) + " 00:00",
        end: extractEndDate(entity) + " 23:59",
        ganttBarConfig: {
          id: entity.id,
          label: entity.name,
          style: {
            background:
              entity.dueDate && entity.dueDate < todayDate
                ? "#e09b69"
                : "#4caf50",
            borderRadius: "5px",
            color: "white",
          },
        },
      },
    ],
  })),
});

export default {
  data() {
    return {
      projects: [],
      tasks: [],
      selectedProjectId: "",
      projectGanttData: [],
      taskGanttData: [],
    };
  },
  methods: {
    loadProjects() {
      fetch(projectEndpoint)
        .then((res) => res.json())
        .then((data) => {
          if (!data.error) {
            this.projects = data;
            this.prepareProjectGanttData();
          }
        })
        .catch((err) => console.error("Error loading projects:", err));
    },
    loadTasksForProject() {
      if (this.selectedProjectId) {
        fetch(`${taskEndpoint}/${this.selectedProjectId}`)
          .then((res) => res.json())
          .then((data) => {
            if (!data.error) {
              this.tasks = data;
              this.prepareTaskGanttData();
            }
          })
          .catch((err) => console.error("Error loading tasks:", err));
      }
    },
    prepareProjectGanttData() {
      this.projectGanttData = getStandardGanttData(this.projects);
    },
    prepareTaskGanttData() {
      this.taskGanttData = getStandardGanttData(this.tasks);
    },
    handleWebSocketMessage(event) {
      const message = JSON.parse(event.data);
      if (message.type === "update") {
        if (message.entity === "project") {
          this.loadProjects();
        } else if (
          message.entity === "task" &&
          message.projectId === this.selectedProjectId
        ) {
          this.loadTasksForProject();
        }
      }
    },
    setupWebSocket() {
      const websocketEndpoint = `${window.location.protocol === "https:" ? "wss" : "ws"}://${window.location.host}/ws`;
      this.ws = new WebSocket(websocketEndpoint);

      this.ws.onopen = () => {
        console.log("WebSocket connection established");
      };

      this.ws.onmessage = this.handleWebSocketMessage;

      this.ws.onerror = (error) => {
        console.error("WebSocket error:", error);
      };

      this.ws.onclose = () => {
        console.log("WebSocket connection closed");
      };
    },
  },
  watch: {
    selectedProjectId: "loadTasksForProject",
  },
  mounted() {
    this.loadProjects();
    this.setupWebSocket();
  },
  beforeUnmount() {
    if (this.ws) {
      this.ws.close();
    }
  },
};
</script>

<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h3>Diagram Gantta - Projekty</h3>
        <g-gantt-chart
          :chart-start="projectGanttData.start"
          :chart-end="projectGanttData.end"
          bar-start="start"
          bar-end="end"
          precision="day"
        >
          <g-gantt-row
            v-for="project in projectGanttData.entities"
            :key="project.id"
            :label="project.label"
            :bars="project.bars"
          />
        </g-gantt-chart>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12" md="6">
        <v-select
          v-model="selectedProjectId"
          :items="projects"
          :item-title="(item) => item.name"
          item-value="id"
          label="Wybierz projekt"
        ></v-select>
      </v-col>
    </v-row>

    <v-row v-if="selectedProjectId">
      <v-col cols="12">
        <h3>Diagram Gantta - Zadania w projekcie</h3>
        <g-gantt-chart
          :chart-start="taskGanttData.start"
          :chart-end="taskGanttData.end"
          bar-start="start"
          bar-end="end"
          precision="day"
        >
          <g-gantt-row
            v-for="task in taskGanttData.entities"
            :key="task.id"
            :label="task.label"
            :bars="task.bars"
          />
        </g-gantt-chart>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
h3 {
  margin-top: 20px;
  text-align: center;
}
</style>
