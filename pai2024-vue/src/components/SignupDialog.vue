<script>
const AUTH_ENDPOINT = "/api/users";

export default {
  data() {
    return {
      isValid: false,
      input: { name: "", surname: "", email: "", password: "" },
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
      fetch(AUTH_ENDPOINT + "/signup", {
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
              this.$emit("close", "Zarejestrowano");
            }
          })
          .catch((err) => {
            console.error(err);
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
      <v-card-title>Utwórz konto</v-card-title>
      <v-card-text>
        <v-text-field
          variant="outlined"
          label="Imie"
          v-model="input.name"
          :rules="[rules.required]"
        />
        <v-text-field
          variant="outlined"
          label="Nazwisko"
          v-model="input.surname"
          :rules="[rules.required]"
        />
        <v-text-field
          variant="outlined"
          label="Adres e-mail"
          v-model="input.email"
          :rules="[rules.required]"
        />
        <v-text-field
          variant="outlined"
          type="password"
          label="Hasło"
          v-model="input.password"
          :rules="[]"
          @keyup.enter="send"
        />
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          color="primary"
          variant="elevated"
          @click="send"
          :disabled="!isValid"
        >
          Zarejestruj
        </v-btn>
        <v-btn variant="elevated" @click="close">Zamknij</v-btn>
      </v-card-actions>
    </v-card>
  </v-form>
</template>

<style scoped></style>
