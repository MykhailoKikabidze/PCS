<script>
import TaskModal from "./TaskModal.vue";
const projectEndpoint = "/api/projects";
const personEndpoint = "/api/users";

export default {
  components: {
    TaskModal,
  },
  data() {
    return {
      isValid: false,
      input: {},
      persons: [],
      rules: {
        startsWithLetter: (value) => {
          const pattern = /^\p{L}/u;
          return pattern.test(value) || "Wymagane zaczynanie się od litery";
        },
        validDate: (value) => {
          const date = new Date(value);
          return !!date || `Wymagana prawidłowa data`;
        },
      },
      persons: [],
      showTaskModal: false, // State to show/hide the TaskModal
    };
  },
  props: ["project"],
  emits: ["close", "listChanged", "popup"],
  methods: {
    send() {
      fetch(projectEndpoint, {
        method: "POST",
        headers: { "Content-type": "application/json" },
        body: JSON.stringify(this.input),
      }).then((res) => {
        res
          .json()
          .then((data) => {
            if (!res.ok) {
              this.$emit("close", data.error, "error");
            } else {
              this.input = {};
              this.$emit("close", `Projekt ${data.name} - dodano`);
              this.$emit("listChanged");
            }
          })
          .catch((err) => {
            this.$emit("close", "Dane odrzucone", "error");
          });
      });
    },
    update() {
      fetch(`${projectEndpoint}/${this.project.id}`, {
        method: "PUT",
        headers: { "Content-type": "application/json" },
        body: JSON.stringify(this.input),
      }).then((res) => {
        res
          .json()
          .then((data) => {
            if (!res.ok) {
              this.$emit("close", data.error, "error");
            } else {
              this.input = {};
              this.$emit("close", `Projekt ${data.name} - zaktualizowano`);
              this.$emit("listChanged");
            }
          })
          .catch((err) => {
            this.$emit("close", "Dane odrzucone", "error");
          });
      });
    },
    remove() {
      fetch(`${projectEndpoint}/${this.project.id}`, {
        method: "DELETE",
      }).then((res) => {
        if (!res.ok) this.$emit("close", res.error, "error");
        else {
          this.input = {};
          this.$emit("close", `Projekt ${this.project.name} - usunięto`);
          this.$emit("listChanged");
        }
      })
      .catch((err) => {
        console.error("Błąd:", err);
      });
    },
    setData(data) {
      this.input = {};
      Object.assign(this.input, data);
    },
    clear() {
      this.input = { _id: this.input._id };
      this.isValid = false;
    },
    close() {
      this.$emit("close");
    },
    taskEditorClose() {
      this.showTaskModal = false;
    },
  },
  mounted() {
    Object.assign(this.input, this.project);
    fetch(
      personEndpoint +
        "?" +
        new URLSearchParams({ sort: "lastName", order: "asc" }).toString(),
    ).then((res) =>
      res.json().then((facet) => {
        if (!facet.error) {
          this.persons = facet;
        }
      }),
    );
  },
};
</script>

<template>
  <v-form v-model="isValid">
    <v-card>
      <v-card-title>{{ this.project.id ? "Edytuj dane" : "Wprowadź dane nowego projektu" }}</v-card-title>
      <v-card-subtitle>
        Dane muszą spełniać odpowiednie reguły, zarówno w tym formularzu, jak i
        w backendzie.
      </v-card-subtitle>
      <v-card-text>
        <v-text-field
          variant="outlined"
          label="Nazwa"
          v-model="input.name"
          :rules="[rules.startsWithLetter]"
        >
        </v-text-field>
        <v-text-field
          type="date"
          variant="outlined"
          label="Data startu"
          v-model="input.startDate"
          :rules="[rules.validDate]"
        >
        </v-text-field>
        <v-text-field
          type="date"
          variant="outlined"
          label="Data końca"
          v-model="input.dueDate"
          :rules="[rules.validDate]"
        >
        </v-text-field>
        <v-autocomplete
          variant="outlined"
          v-model="input.userEmails"
          :items="persons"
          :item-title="(item) => item.name + ' ' + item.surname"
          item-value="email"
          label="Wykonawcy"
          multiple
          chips
        ></v-autocomplete>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <!-- Task management button -->
        <v-btn
          v-if="this.project.id"
          variant="elevated"
          color="primary"
          @click="showTaskModal = true"
        >
          Zarządzaj zadaniami
        </v-btn>
        <v-btn variant="elevated" @click="clear">Zeruj</v-btn>
        <v-btn
          color="primary"
          variant="elevated"
          @click="send"
          :disabled="!isValid"
          v-if="!this.project.id"
          >Wyślij</v-btn
        >
        <v-btn
          color="secondary"
          variant="elevated"
          @click="update"
          :disabled="!isValid"
          v-if="this.project.id"
          >Aktualizuj</v-btn
        >
        <v-btn color="error" variant="elevated" @click="remove" v-if="this.project.id">Usuń</v-btn>
        <v-btn variant="elevated" @click="close">Zamknij</v-btn>
      </v-card-actions>
    </v-card>

    <!-- Task Modal -->
    <TaskModal
      v-if="showTaskModal"
      :project="this.project"
      @close="taskEditorClose"
      @popup="(text, color) => $emit('popup', text, color)"
    />
  </v-form>
</template>

<style scoped></style>
