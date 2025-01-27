import axios from "axios";
import { useState } from "react";

const CurrencyExchangeForm = () => {
  const [amount, setAmount] = useState("");
  const [fromCurrency, setFromCurrency] = useState("DKK");
  const [toCurrency, setToCurrency] = useState("USD");
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const switchCurrencies = () => {
    setFromCurrency(toCurrency);
    setToCurrency(fromCurrency);
    setResult(null);
  };

  const handleConvert = async () => {
    setError("");
    setResult(null);
    if (!amount || Number(amount) <= 0) {
      setError("Please enter a valid positive number.");
      return;
    }
    setLoading(true);
    try {
      const response = await axios.get(
        `http://localhost:8080/currency/conversion/${fromCurrency}/${toCurrency}/${amount}`
      );
      setResult(response.data);
    } catch (err) {
      console.error(err);
      setError("Failed to convert currency. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-4">
      <h2 className="text-xl font-bold mb-4">Currency Conversion</h2>
      <div className="flex md:flex-row flex-col space-x-4 space-y-4">
        <input
          id="amount"
          type="number"
          min="0"
          value={amount}
          onChange={(e) => {
            setAmount(e.target.value);
            setResult(null);
          }}
          className="p-2 border rounded-lg"
          placeholder="Enter amount"
        />

        <button
          type="button"
          onClick={switchCurrencies}
          className="bg-blue-500 text-white py-2 px-2 rounded-lg hover:bg-blue-600"
        >
          {fromCurrency} to {toCurrency}
        </button>

        <button
          type="button"
          onClick={handleConvert}
          className={`py-2 px-2 ${
            loading
              ? "bg-gray-400 cursor-not-allowed"
              : "bg-green-500 hover:bg-green-600 text-white"
          }`}
          disabled={loading}
        >
          {loading ? "Converting..." : "Convert"}
        </button>

        {result !== null && (
          <div className="p-2 bg-green-50 border border-green-500 rounded-lg">
            <p className="font-medium text-green-700">
              {amount} {fromCurrency} = {result} {toCurrency}
            </p>
          </div>
        )}

        {error && (
          <div className="p-2 bg-red-50 border border-red-500 rounded-lg">
            <p className="font-medium text-red-700">{error}</p>
          </div>
        )}

        <div />
      </div>
    </div>
  );
};

export default CurrencyExchangeForm;
