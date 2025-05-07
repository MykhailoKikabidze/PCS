<script>
const ENDPOINT = "/api/users/";

export default {
  data() {
    return {
      isValid: false,
      input: { name: "", surname: "", password: "" },
      rules: {
        required: (value) => {
          return value.length > 0 || "Pole wymagane";
        },
      },
    };
  },
  emits: ["close"],
  props: ["user"],
  created() {
    this.input = {
      name: this.user.name,
      surname: this.user.surname,
      password: this.user.password,
    };
  },
  methods: {
    deleteUser() {
      fetch(ENDPOINT + this.user.email, {
        method: "DELETE",
        headers: { "Content-type": "application/json" },
      }).then((res) => {
        res
          .json()
          .then((data) => {
            if (!res.ok) {
              this.$emit("close", data.error, "error");
            } else {
              this.input = {};
              this.$emit("close", "Usunięto konto");
            }
          })
          .catch((err) => {
            console.error(err);
            this.$emit("close", "Brak połączenia z backendem", "error");
          });
      });
    },
    save() {
      fetch(ENDPOINT + this.user.email, {
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
              this.$emit("close", "Zalogowano");
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
  <div>
    <v-card-title>Ustawienia</v-card-title>
    <v-card-text>
      <v-form v-model="isValid">
        <v-text-field
          variant="outlined"
          label="Adres e-mail"
          v-model="user.email"
          :rules="[rules.required]"
          disabled
        />
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
          type="password"
          label="Hasło"
          v-model="input.password"
          :rules="[]"
        />
        <v-btn color="primary" @click="save">Zapisz</v-btn>
      </v-form>
      <br />
      <hr />
      <br />
      <v-btn color="error" variant="elevated" @click="deleteUser">
        Usuń konto
      </v-btn>
    </v-card-text>
  </div>
</template>
