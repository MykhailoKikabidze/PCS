<script>
const authEndpoint = "/api/users";

export default {
  data() {
    return {
      isValid: false,
      input: { email: "", password: "" },
      rules: {
        required: (value) => {
          return value.length > 0 || "Pole wymagane";
        },
      },
    };
  },
  emits: ["close"],
  methods: {
    send() {
      fetch(authEndpoint + "/login", {
        method: "POST",
        headers: { "Content-type": "application/json" },
        body: JSON.stringify(this.input),
      }).then((res) => {
        res
          .text()
          .then((data) => {
            if (!res.ok) {
              this.$emit("close", data, "error");
            } else {
              this.input = {};
              this.$emit("close", "Zalogowano");
            }
          })
          .catch((err) => {
            this.$emit("close", "Brak połączenia z backendem", "error");
          });
      });
    },
    close() {
      this.$emit("close");
    },
  },
};
</script>

<template>
  <v-form v-model="isValid">
    <v-card>
      <v-card-title>Login</v-card-title>
      <v-card-text>
        <v-text-field
          variant="outlined"
          label="Nazwa użytkownika"
          v-model="input.email"
          :rules="[rules.required]"
        >
        </v-text-field>
        <v-text-field
          variant="outlined"
          type="password"
          label="Hasło"
          v-model="input.password"
          :rules="[]"
          @keyup.enter="send"
        >
        </v-text-field>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          color="primary"
          variant="elevated"
          @click="send"
          :disabled="!isValid"
          >Logowanie</v-btn
        >
        <v-btn variant="elevated" @click="close">Zamknij</v-btn>
      </v-card-actions>
    </v-card>
  </v-form>
</template>

<style scoped></style>
