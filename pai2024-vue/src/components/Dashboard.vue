<script>
import common from "../mixins/common";

export default {
  props: ["user"],
  emits: ["popup"],
  mixins: [common],
  mounted() {
    console.log(this.user);
  },
};
</script>

<template>
  <h1>Pulpit</h1>
  <p v-if="this.user.name">Witam, {{ this.user.name }}</p>
  <i v-if="!this.user.name">niezalogowany</i>
</template>
