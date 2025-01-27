import axios from "axios";
import { useEffect, useState } from "react";
import AccountDepositeDialog from "./AccountDepositeDialog";

interface Account {
  accountNumber: string;
  firstName: string;
  lastName: string;
  balance: number;
}

const AccountList = () => {
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [selectedAccount, setSelectedAccount] = useState<Account | null>(null);

  useEffect(() => {
    fetchAccounts();
  }, []);

  const fetchAccounts = () => {
    axios.get("http://localhost:8080/account").then((response) => {
      setAccounts(response.data);
    });
  };

  return (
    <div className="p-4">
      <h2 className="text-xl font-bold mb-4">Accounts</h2>
      <ul className="space-y-2">
        {accounts.map((account) => (
          <div
            className="p-4 border rounded-lg shadow-md flex md:flex-row flex-col justify-between items-center"
            key={account.accountNumber}
          >
            <li key={account.accountNumber}>
              <p>
                <strong>Account Number:</strong> {account.accountNumber}
              </p>
              <p>
                <strong>Name:</strong> {account.firstName} {account.lastName}
              </p>
              <p>
                <strong>Balance:</strong> {account.balance} DKK
              </p>
            </li>
            <button
              className="bg-blue-500 text-white py-2 px-4 mt-2 rounded-lg hover:bg-blue-600"
              onClick={() => setSelectedAccount(account)}
            >
              Add Money
            </button>
          </div>
        ))}
      </ul>

      {/* Deposit Dialog */}
      {selectedAccount && (
        <AccountDepositeDialog
          account={selectedAccount}
          onClose={() => setSelectedAccount(null)}
          onSuccess={fetchAccounts}
        />
      )}
    </div>
  );
};

export default AccountList;
