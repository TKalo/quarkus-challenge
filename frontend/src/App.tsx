import CurrencyExchangeForm from "./components/CurrencyExcangeForm";

const App = () => {
  return (
    <div className="w-screen h-full justify-center items-center">
      <div className="bg-gray-800 p-8">
        <div className="md:text-5xl text-4xl font-bold text-white mb-8 text-center">
          Bank Account Manager
        </div>
        <CurrencyExchangeForm />
      </div>
    </div>
  );
};

export default App;
