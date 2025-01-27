import axios from "axios";
import React, { useState } from "react";

interface DepositDialogProps {
  account: { accountNumber: string; firstName: string; lastName: string };
  onClose: () => void;
  onSuccess: () => void;
}

const AccountDepositeDialog: React.FC<DepositDialogProps> = ({
  account,
  onClose,
  onSuccess,
}) => {
  const [amount, setAmount] = useState<number | string>("");
  const [error, setError] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);

  const handleDeposit = async () => {
    if (!amount || Number(amount) <= 0) {
      setError("Please enter a valid positive number.");
      return;
    }

    setLoading(true);
    setError("");

    try {
      await axios.post(
        `http://localhost:8080/account/${account.accountNumber}/deposit`,
        { amount: amount }
      );
      onSuccess();
      onClose();
    } catch {
      setError("Failed to deposit. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleOutsideClick = (e: React.MouseEvent<HTMLDivElement>) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  return (
    <div
      className="fixed inset-0 flex items-center justify-center"
      onClick={handleOutsideClick}
    >
      <div className="bg-gray-900 p-6 rounded-lg shadow-lg w-80">
        <h3 className="text-lg font-bold mb-4">
          Add Money to {account.firstName} {account.lastName}
        </h3>
        <input
          type="number"
          min="0"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          className="p-2 border rounded-lg w-full mb-4"
          placeholder="Enter amount"
        />
        {error && <p className="text-red-500 text-sm mb-2">{error}</p>}
        <div className="flex justify-end space-x-2">
          <button
            onClick={onClose}
            className="bg-gray-300 text-white py-2 px-4 rounded-lg hover:bg-gray-400"
          >
            Cancel
          </button>
          <button
            onClick={handleDeposit}
            disabled={loading}
            className={`py-2 px-4 rounded-lg ${
              loading
                ? "bg-gray-400 cursor-not-allowed"
                : "bg-green-500 hover:bg-green-600 text-white"
            }`}
          >
            {loading ? "Processing..." : "Submit"}
          </button>
        </div>
      </div>
    </div>
  );
};

export default AccountDepositeDialog;
