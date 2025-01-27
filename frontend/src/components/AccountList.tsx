import axios from "axios";
import { useEffect, useState } from "react";

interface Account {
  accountNumber: string;
  firstName: string;
  lastName: string;
  balance: number;
}

const AccountList = () => {
  const [accounts, setAccounts] = useState<Account[]>([]);

  useEffect(() => {
    axios.get("http://localhost:8080/account").then((response) => {
      console.log(response.data);
      setAccounts(response.data);
    });
  }, []);

  return (
    <div className="p-4">
      <h2 className="text-xl font-bold mb-4">Accounts</h2>
      <ul className="space-y-2">
        {accounts.map((account) => (
          <li
            key={account.accountNumber}
            className="p-4 border rounded-lg shadow-md"
          >
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
        ))}
      </ul>
    </div>
  );
};

export default AccountList;
