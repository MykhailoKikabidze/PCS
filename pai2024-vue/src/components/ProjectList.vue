<script>
import common from "../mixins/common";
import ProjectEditor from "./ProjectEditor.vue";

const projectEndpoint = "/api/projects";

export default {
  components: { ProjectEditor },
  mixins: [common],
  props: ["user"],
  emits: ["popup"],
  data() {
    return {
      projects: {},
      project: {},
      editor: false,
      itemsPerPage: 10,
      headers: [
        { title: "Nazwa", key: "name", align: "start", sortable: true },
        {
          title: "Data startu",
          key: "startDate",
          align: "end",
          sortable: true,
        },
        { title: "Data końca", key: "dueDate", align: "end", sortable: true },
        {
          title: "Wykonawcy",
          key: "contractors",
          align: "start",
          sortable: false,
        },
      ],
      loading: false,
      search: "",
      itemsLength: 0,
      serverItems: [],
      tableKey: 0,
    };
  },
  methods: {
    checkIfInRole: () => true,
    loadItems({ page, itemsPerPage, sortBy }) {
      this.loading = true;
      const skip = (page - 1) * itemsPerPage;
      let queryString = { skip, limit: itemsPerPage, search: this.search };
      if (sortBy && sortBy[0]) {
        queryString.sort = sortBy[0].key;
        queryString.order = sortBy[0].order;
      } else {
        queryString.sort = "name";
        queryString.order = "asc";
      }
      fetch(projectEndpoint + "?" + new URLSearchParams(queryString))
        .then(res => res.json())
        .then(list => {
          // list — это ProjectResponse[]
          this.itemsLength = list.length
          // если вы хотите в колонке «Wykonawcy» выводить userEmails:
          this.serverItems = list.map(p => ({
            ...p,
            contractors: p.userInitials  
          }))
          this.loading = false
        })
    },
    clickItem(item, event) {
      this.project = event.item;
      this.editor = true;
    },
    add() {
      this.project = {};
      this.editor = true;
    },
    editorClose(text, color) {
      this.editor = false;
      if (text) {
        this.$emit("popup", text, color);
      }
    },
  },
};
</script>

<template>
  <v-card variant="outlined" v-if="checkIfInRole(user, [0, 1])">
    <v-card-title class="d-flex">
      Projekty
      <v-spacer></v-spacer>
      <v-btn @click="add" v-if="checkIfInRole(user, [0])">Dodaj</v-btn>
    </v-card-title>
    <v-card-text>
      <v-data-table-server v-model:items-per-page="itemsPerPage" :headers="headers" :items="serverItems"
        :items-length="itemsLength" :loading="loading" :search="search" :key="tableKey" @update:options="loadItems"
        @click:row="clickItem" itemsPerPageText="# elementów na stronie" pageText="{0}-{1} z {2}">
        <template #item.birthDate="{ item }">
          {{ new Date(item.birthDate).toLocaleDateString() }}
        </template>
        <template #item.contractors="{ item }">
          {{ item.contractors.join(" ") }}
        </template>
        <template #footer.prepend>
          <v-text-field v-model="search" class="mr-5" variant="outlined" density="compact" placeholder="szukaj..."
            hide-details prepend-icon="mdi-magnify"></v-text-field>
        </template>
      </v-data-table-server>
    </v-card-text>
  </v-card>

  <v-dialog v-model="editor" width="50%" v-if="checkIfInRole(user, [0])">
    <ProjectEditor :project="project" @close="editorClose" @list-changed="tableKey++" @popup="(text, color) => $emit('popup', text, color)"/>
  </v-dialog>
</template>

<style scoped></style>
