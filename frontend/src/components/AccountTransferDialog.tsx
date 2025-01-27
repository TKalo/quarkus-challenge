import axios from "axios";
import React, { useState } from "react";

interface Account {
  accountNumber: string;
  firstName: string;
  lastName: string;
  balance: number;
}

interface TransferDialogProps {
  baseAccount: Account;
  accounts: Account[];
  onClose: () => void;
  onSuccess: () => void;
}

const TransferDialog: React.FC<TransferDialogProps> = ({
  baseAccount,
  accounts,
  onClose,
  onSuccess,
}) => {
  const [toAccountNumber, setToAccountNumber] = useState<string>("");
  const [amount, setAmount] = useState<number | string>("");
  const [error, setError] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);

  const handleTransfer = async () => {
    setError("");

    if (!toAccountNumber) {
      setError("Please select an account to transfer to.");
      return;
    }

    if (!amount || Number(amount) <= 0) {
      setError("Please enter a valid positive number.");
      return;
    }

    if (Number(amount) > baseAccount.balance) {
      setError(
        "Transfer amount cannot exceed the balance of the base account."
      );
      return;
    }

    setLoading(true);

    try {
      await axios.post("http://localhost:8080/account/transfer", {
        fromAccount: baseAccount.accountNumber,
        toAccount: toAccountNumber,
        amount: Number(amount),
      });
      onSuccess();
      onClose();
    } catch (err) {
      console.error(err);
      setError("Failed to complete the transfer. Please try again.");
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
      <div className="bg-gray-900 p-6 rounded-lg shadow-lg w-96">
        <h3 className="text-lg font-bold mb-4">
          Transfer Money from {baseAccount.firstName} {baseAccount.lastName}
        </h3>

        <div className="mb-4 justify-center items-center flex flex-col">
          Current balance: {baseAccount.balance}
        </div>

        <div className="mb-4">
          <label className="block font-medium mb-1">To Account:</label>
          <select
            value={toAccountNumber}
            onChange={(e) => setToAccountNumber(e.target.value)}
            className="p-2 border rounded-lg w-full bg-gray-900"
          >
            <option value="">Select an account</option>
            {accounts
              .filter(
                (account) => account.accountNumber !== baseAccount.accountNumber
              )
              .map((account) => (
                <option
                  key={account.accountNumber}
                  value={account.accountNumber}
                >
                  {account.firstName} {account.lastName} (
                  {account.accountNumber})
                </option>
              ))}
          </select>
        </div>

        <div className="mb-4">
          <label className="block font-medium mb-1">Amount:</label>
          <input
            type="number"
            min="0"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            className="p-2 border rounded-lg w-full"
            placeholder="Enter amount"
          />
        </div>

        {error && <p className="text-red-500 text-sm mb-4">{error}</p>}

        <div className="flex justify-end space-x-2">
          <button
            onClick={onClose}
            className="bg-gray-300 text-white py-2 px-4 rounded-lg hover:bg-gray-400"
          >
            Cancel
          </button>
          <button
            onClick={handleTransfer}
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

export default TransferDialog;
