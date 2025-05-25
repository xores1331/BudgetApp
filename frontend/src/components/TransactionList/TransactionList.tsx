import React, { useEffect, useState } from "react";
import { Transaction } from "../../types/transaction";
import styles from "./TransactionList.module.scss";
import { toast } from "react-toastify";
import graphqlClient from "../../api/graphClient";
import { useBalance } from "../BalanceBar/useBalance";

const TransactionList: React.FC = () => {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>("");
  const [editingTransactionId, setEditingTransactionId] = useState<
    number | null
  >(null);
  const [editValues, setEditValues] = useState<Partial<Transaction>>({});
  const [editErrors, setEditErrors] = useState<{ [key: string]: string }>({});
  const [showDeleteModal, setShowDeleteModal] = useState<boolean>(false);
  const [transactionToDelete, setTransactionToDelete] = useState<number | null>(
    null
  );
  const { refreshBalance } = useBalance();
  useEffect(() => {
    fetchTransactions();
  }, []);

  const fetchTransactions = async () => {
    const query = `
      query {
        transactions {
          id
          amount
          type
          tags
          notes
          timestamp
        }
      }
    `;
    try {
      const response = await graphqlClient(query);
      const transactionsData = response?.data?.transactions || [];
      setTransactions(transactionsData);
    } catch (err) {
      setError("Nie udało się pobrać transakcji." + err);
    } finally {
      setLoading(false);
    }
  };

  const startEdit = (transaction: Transaction) => {
    setEditingTransactionId(transaction.id);
    setEditValues({ ...transaction });
    setEditErrors({});
  };

  const handleEditChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>,
    field: keyof Transaction
  ) => {
    setEditValues((prev) => ({ ...prev, [field]: e.target.value }));
  };

  const validateEdit = (): boolean => {
    const errors: { [key: string]: string } = {};
    const { amount, type, tags, notes } = editValues;

    if (!amount || parseFloat(amount.toString()) <= 0) {
      errors.amount = "Kwota musi być większa od zera.";
    }

    if (type !== "INCOME" && type !== "EXPENSE") {
      errors.type = "Nieprawidłowy typ transakcji.";
    }

    if (!tags || tags.trim().length < 2) {
      errors.tags = "Tag musi mieć co najmniej 2 znaki.";
    }

    if (notes && notes.length > 200) {
      errors.notes = "Notatka może mieć maksymalnie 200 znaków.";
    }

    setEditErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const updateTransaction = async () => {
    if (!editingTransactionId || !editValues) return;
    if (!validateEdit()) return;

    const mutation = `
      mutation UpdateTransaction($id: ID!, $transactionDTO: TransactionInput!) {
        updateTransaction(id: $id, transactionDTO: $transactionDTO) {
          id
        }
      }
    `;

    try {
      const response = await graphqlClient(mutation, {
        id: editingTransactionId,
        transactionDTO: {
          amount: Number(editValues.amount),
          type: editValues.type,
          tags: editValues.tags,
          notes: editValues.notes ?? "",
        },
      });

      const errors = response?.errors;

      if (errors && errors.length > 0) {
        errors.forEach((err: { message: string }) => {
          toast.error(err.message);
        });
        return;
      }

      // Zaktualizuj listę lokalnie
      setTransactions(
        transactions.map((t) =>
          t.id === editingTransactionId ? { ...t, ...editValues } : t
        )
      );
      toast.success("✅ Transakcja zaktualizowana!");
      setEditingTransactionId(null);
      setEditErrors({});
      refreshBalance(null);
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
    } catch (error: any) {
      toast.error((error as Error).message);
    }
  };

  const openDeleteModal = (id: number) => {
    setTransactionToDelete(id);
    setShowDeleteModal(true);
  };

  const deleteTransaction = async () => {
    if (!transactionToDelete) return;

    const mutation = `
      mutation DeleteTransaction($id: ID!) {
        deleteTransaction(id: $id)
      }
    `;
    try {
      await graphqlClient(mutation, { id: transactionToDelete });
      setTransactions(transactions.filter((t) => t.id !== transactionToDelete));
      refreshBalance(null);
      toast.success("✅ Transakcja usunięta!");
    } catch (error) {
      toast.error((error as Error).message);
    }
    setShowDeleteModal(false);
    setTransactionToDelete(null);
  };

  if (loading) return <p>Ładowanie...</p>;
  if (error) return <p className="text-red-500">{error}</p>;

  return (
    <div className={styles["transaction-list"]}>
      <h2>Lista Transakcji</h2>
      <table>
        <thead>
          <tr>
            <th>Kwota</th>
            <th>Typ</th>
            <th>Tagi</th>
            <th>Notatki</th>
            <th>Akcje</th>
          </tr>
        </thead>
        <tbody>
          {transactions.map((transaction) => (
            <tr key={transaction.id}>
              <td>
                {editingTransactionId === transaction.id ? (
                  <>
                    <input
                      type="number"
                      value={editValues.amount || ""}
                      onChange={(e) => handleEditChange(e, "amount")}
                    />
                    {editErrors.amount && (
                      <p className={styles.error}>{editErrors.amount}</p>
                    )}
                  </>
                ) : (
                  `${transaction.amount} zł`
                )}
              </td>
              <td>
                {editingTransactionId === transaction.id ? (
                  <>
                    <select
                      value={editValues.type || ""}
                      onChange={(e) => handleEditChange(e, "type")}
                    >
                      <option value="">-- wybierz --</option>
                      <option value="INCOME">Przychód</option>
                      <option value="EXPENSE">Wydatek</option>
                    </select>
                    {editErrors.type && (
                      <p className={styles.error}>{editErrors.type}</p>
                    )}
                  </>
                ) : transaction.type === "INCOME" ? (
                  "Przychód"
                ) : (
                  "Wydatek"
                )}
              </td>
              <td>
                {editingTransactionId === transaction.id ? (
                  <>
                    <input
                      type="text"
                      value={editValues.tags || ""}
                      onChange={(e) => handleEditChange(e, "tags")}
                    />
                    {editErrors.tags && (
                      <p className={styles.error}>{editErrors.tags}</p>
                    )}
                  </>
                ) : (
                  transaction.tags
                )}
              </td>
              <td>
                {editingTransactionId === transaction.id ? (
                  <>
                    <input
                      type="text"
                      value={editValues.notes || ""}
                      onChange={(e) => handleEditChange(e, "notes")}
                    />
                    {editErrors.notes && (
                      <p className={styles.error}>{editErrors.notes}</p>
                    )}
                  </>
                ) : (
                  transaction.notes
                )}
              </td>
              <td>
                {editingTransactionId === transaction.id ? (
                  <>
                    <button className={styles.save} onClick={updateTransaction}>
                      Zapisz
                    </button>
                    <button
                      className={styles.cancel}
                      onClick={() => setEditingTransactionId(null)}
                    >
                      Anuluj
                    </button>
                  </>
                ) : (
                  <>
                    <button
                      className={styles.edit}
                      onClick={() => startEdit(transaction)}
                    >
                      Edytuj
                    </button>
                    <button
                      className={styles.delete}
                      onClick={() => openDeleteModal(transaction.id)}
                    >
                      Usuń
                    </button>
                  </>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {showDeleteModal && (
        <div className={styles.modal}>
          <div className={styles["modal-content"]}>
            <p>Czy na pewno chcesz usunąć transakcję?</p>
            <div className={styles["modal-buttons"]}>
              <button className={styles.confirm} onClick={deleteTransaction}>
                Tak
              </button>
              <button
                className={styles.cancel}
                onClick={() => setShowDeleteModal(false)}
              >
                Anuluj
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default TransactionList;
