<script>
const taskEndpoint = "/api/tasks";
const projectEndpoint = "/api/projects";

export default {
  props: ["project"],
  emits: ["close", "popup"],
  data() {
    return {
      isDialogOpen: true,
      tasks: [],
      input: {
        name: "",
        startDate: "",
        endDate: "",
        assignee_ids: [],
      },
      rules: {
        startsWithLetter: (value) => {
          const pattern = /^\p{L}/u;
          return pattern.test(value) || "Wymagane zaczynanie się od litery";
        },
        validDate: (value) => {
          const date = new Date(value);
          return !!date || `Wymagana prawidłowa data`;
        },
        dueDateAfterStartDate: (dueDate) => {
          const startDate = new Date(this.input.startDate);
          const endDate = new Date(dueDate);
          if (!dueDate || !this.input.startDate) return true;
          if (isNaN(startDate) || isNaN(endDate)) return true;
          return endDate >= startDate || "Data końca musi być po dacie startu";
        },
      },
      isEditing: false, 
      selectedTaskId: null,
      loadPersonList: true,
    };
  },
  methods: {
    loadTasks() {
      fetch(`${taskEndpoint}/${this.project.id}`)
        .then((res) => res.json())
        .then((data) => {
          if (!data.error) this.tasks = data;
        })
        .catch((err) => console.error("Error loading tasks:", err));
    },
    loadPersons() {
      fetch(`${projectEndpoint}/${this.project.id}/users`)
        .then((res) => res.json())
        .then((data) => {
          if (!data.error) this.persons = data;
          this.loadPersonList = false;
        })
        .catch((err) => console.error("Error loading persons:", err.message));
    },
    saveTask() {
      let url = taskEndpoint;
      let method = "POST";

      if (this.isEditing && this.selectedTaskId) {
        url = `${taskEndpoint}/${this.selectedTaskId}`;
        method = "PUT";
      }

      const body = {
        name: this.input.name,
        startDate: this.input.startDate,
        endDate: this.input.endDate,
        assignee_ids: this.input.assignee_ids,
        project_id: this.project.id,
        ...(method === "POST" ? { project_id: this.project.id } : {})
      };

      fetch(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
      })
        .then(res => res.json())
        .then(data => {
          if (!data.error) {
            this.loadTasks();
            this.resetInput();
          } else {
            console.error("Ошибка от сервера:", data.error);
          }
        })
        .catch(err => console.error("Error saving task:", err));
    },
    deleteTask(task) {
      fetch(`${taskEndpoint}/${task.id}`, { method: "DELETE" })
      .then((res) => {
        if (!res.ok) this.$emit("popup", "Błąd", "error");
        else {
          this.input = {};
          this.$emit("popup", `Task ${task.name} - usunięto`)
          this.loadTasks();
        }
      })
      .catch((err) => { console.error("Błąd:", err); });
    },
    editTask(task) {
      this.isEditing = true;
      this.selectedTaskId = task.id;
      this.input = {
        name: task.name,
        startDate: task.startDate,
        endDate: task.dueDate,
        assignee_ids: task.assignees 
    ? task.assignees.map(a => a.id) 
    : [],  
      };
    },
    resetInput() {
      this.isEditing = false;
      this.selectedTaskId = null;
      this.input = {
        name: "",
        startDate: "",
        endDate: "",
        assignee_ids: [],
      };
      this.$nextTick(() => {
        this.$refs.taskForm?.resetValidation();
      });
    },
    closeModal() {
      this.$emit("close");
    },
  },
  mounted() {
    this.loadTasks();
    this.loadPersons();
  },
};
</script>

<template>
  <v-dialog v-if="!loadPersonList" v-model="isDialogOpen" persistent max-width="800px">
    <v-card>
      <v-card-title>Zarządzaj Zadaniami</v-card-title>
      <v-card-text>
        <!-- Task List -->
        <v-list>
          <v-list-item v-for="task in tasks" :key="task.id">
            <div>
              <div>{{ task.name }}</div>
              <small>
                Start:
                {{ new Date(task.startDate).toLocaleDateString() }}
                -
                {{
                  task.dueDate
                    ? "End: " + new Date(task.dueDate).toLocaleDateString()
                    : "in progress"
                }}
              </small>
            </div>
            <template v-slot:append>
              <v-btn icon @click="editTask(task)" class="mr-2">
                <v-icon>mdi-pencil</v-icon>
              </v-btn>
              <v-btn icon @click="deleteTask(task)">
                <v-icon>mdi-delete</v-icon>
              </v-btn>
            </template>
          </v-list-item>
        </v-list>

        <!-- Task Form -->
        <v-form ref="taskForm">
          <v-text-field
            v-model="input.name"
            label="Nazwa Zadania"
            :rules="[rules.startsWithLetter]"
          ></v-text-field>
          <v-text-field
            type="date"
            v-model="input.startDate"
            label="Data Rozpoczęcia"
            :rules="[rules.validDate]"
          ></v-text-field>
          <v-text-field
            type="date"
            v-model="input.endDate"
            label="Data Zakończenia"
            :rules="[rules.validDate, rules.dueDateAfterStartDate]"
          ></v-text-field>
          <v-autocomplete
            v-model="input.assignee_ids"
            :items="persons"
            :item-title="(item) => item.name + ' ' + item.surname"
            item-value="id"
            label="Wybierz Wykonawców"
            multiple
          ></v-autocomplete>
          <v-btn @click="saveTask" color="primary" class="mr-2">
            {{ isEditing ? "Zaktualizuj" : "Dodaj" }} Zadanie
          </v-btn>
          <v-btn @click="resetInput">Anuluj</v-btn>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="error" @click="closeModal">Zamknij</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped></style>
