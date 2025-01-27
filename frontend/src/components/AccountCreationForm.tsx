import axios from "axios";
import { useState } from "react";

const AccountCreationForm = () => {
  const [formData, setFormData] = useState({ firstName: "", lastName: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = (e: { preventDefault: () => void }) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    axios
      .post("http://localhost:8080/account", formData)
      .then(() => {
        window.location.reload();
        setFormData({ firstName: "", lastName: "" });
      })
      .catch((err) => {
        console.error(err);
        const message =
          err.response?.data?.message || "Failed to create the account";
        setError(message);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  return (
    <div className="p-4">
      <h2 className="text-xl font-bold mb-4">Create Account</h2>
      <form
        onSubmit={handleSubmit}
        className="flex md:flex-row flex-col space-y-4 space-x-4"
      >
        <input
          type="text"
          placeholder="First Name"
          value={formData.firstName}
          onChange={(e) =>
            setFormData({ ...formData, firstName: e.target.value })
          }
          className="p-2 border rounded-lg w-full min-w-[200px]"
          required
        />
        <input
          type="text"
          placeholder="Last Name"
          value={formData.lastName}
          onChange={(e) =>
            setFormData({ ...formData, lastName: e.target.value })
          }
          className="p-2 border rounded-lg w-full min-w-[200px]"
          required
        />
        <button
          type="submit"
          className={`py-2 px-4 rounded-lg ${
            loading
              ? "bg-gray-400 cursor-not-allowed"
              : "bg-blue-500 hover:bg-blue-600 text-white"
          }`}
          disabled={loading}
        >
          {loading ? "Creating..." : "Create Account"}
        </button>
      </form>
      {error && <p className="text-red-500 font-semibold mb-4">{error}</p>}
    </div>
  );
};

export default AccountCreationForm;
