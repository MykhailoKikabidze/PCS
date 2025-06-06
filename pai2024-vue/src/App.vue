<script>
import common from "./mixins/common";
import LoginDialog from "./components/LoginDialog.vue";
import LogoutDialog from "./components/LogoutDialog.vue";
import SignupDialog from "./components/SignupDialog.vue";

const authEndpoint = "/api/users/me";

export default {
  mixins: [common],
  components: { LoginDialog, LogoutDialog, SignupDialog },
  data() {
    return {
      snackbar: { on: false },
      generalError: false,
      user: null,
      loginDialog: false,
      logoutDialog: false,
      signupDialog: false,
      loadingUser: true,
    };
  },
  methods: {
    onPopup(text, color = "success") {
      this.snackbar.text = text;
      this.snackbar.color = color;
      this.snackbar.on = true;
    },
    onLogin(text, color = "success") {
      this.loginDialog = false;
      this.logoutDialog = false;
      this.signupDialog = false;
      if (color == "success") {
        this.whoami();
        this.$router.push("/");
      }
      if (text) {
        this.onPopup(text, color);
      }
    },
    whoami() {
      fetch(authEndpoint)
        .then((res) => {
          if (!res.ok) {
            this.generalError = true;
            return;
          }
          res.json().then((data) => {
            this.user = data;
            this.loadingUser = false;
          });
        })
        .catch((err) => {
          this.generalError = true;
          this.loadingUser = false;
        });
    },
  },
  mounted() {
    this.whoami();
  },
};
</script>

<template>
  <v-app v-if="!loadingUser && !generalError">
    <!-- <v-app> -->
    <v-navigation-drawer expand-on-hover rail permanent>
      <v-list nav>
        <v-list-item
          v-for="route in $router.options.routes"
          :to="route.path"
          :title="route.title"
          :prepend-icon="route.icon"
          v-show="route.public || this.user?.name"
          exact
        ></v-list-item>
      </v-list>

      <v-spacer></v-spacer>

      <v-list nav>
        <v-list-item
          key="Login"
          @click="loginDialog = true"
          @close="onLogin"
          prepend-icon="mdi-login"
          title="Login"
          exact
          v-if="this.user && !this.user.name"
        />
        <v-list-item
          key="Login"
          @click="signupDialog = true"
          @close="onLogin"
          prepend-icon="mdi-account-plus"
          title="Utwórz konto"
          exact
          v-if="this.user && !this.user.name"
        />
        <v-list-item
          key="Logout"
          @click="logoutDialog = true"
          @close="onLogin"
          prepend-icon="mdi-logout"
          title="Logout"
          exact
          v-if="this.user && this.user.name"
        />
      </v-list>
    </v-navigation-drawer>

    <v-main>
      <router-view :user="this.user" @popup="onPopup"/>
    </v-main>

    <v-dialog v-model="signupDialog" width="33%">
      <SignupDialog @close="onLogin" />
    </v-dialog>

    <v-dialog v-model="loginDialog" width="33%">
      <LoginDialog @close="onLogin" />
    </v-dialog>

    <v-dialog v-model="logoutDialog" width="33%">
      <LogoutDialog @close="onLogin" />
    </v-dialog>

    <v-snackbar v-model="snackbar.on" :color="snackbar.color" :timeout="3000">
      <div style="width: 100%; text-align: center">{{ snackbar.text }}</div>
    </v-snackbar>
  </v-app>

  <v-snackbar
    v-model="generalError"
    color="error"
    location="center"
    timeout="-1"
  >
    <div style="width: 100%; text-align: center">
      Brak połączenia z backendem
    </div>
  </v-snackbar>
</template>

<style scoped></style>
