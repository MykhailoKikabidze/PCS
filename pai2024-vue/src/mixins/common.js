export default {
  methods: {
    checkIsLoggedIn(user) {
      return !!user?.name;
    },
  },
};
