package com.cts.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cts.model.Cheque;
import com.cts.util.DbConnection;

public class ChequeDaoImpl implements ChequeDao {

    @Override
    public void insertCheque(Cheque cheque) {
        String sql = "INSERT INTO cheques (drawer_name, bank_name, branch_name, micr_code, " +
                     "cheque_number, account_number, amount_digits, amount_words, " +
                     "cheque_date, payee_name, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'SUBMITTED')";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cheque.getDrawerName());
            ps.setString(2, cheque.getBankName());
            ps.setString(3, cheque.getBranchName());
            ps.setString(4, cheque.getMicrCode());
            ps.setString(5, cheque.getChequeNumber());
            ps.setString(6, cheque.getAccountNumber());
            ps.setString(7, cheque.getAmountDigits());
            ps.setString(8, cheque.getAmountWords());
            ps.setString(9, cheque.getChequeDate());
            ps.setString(10, cheque.getPayeeName());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            // FIX: rethrow so UserController can show error to user
            throw new RuntimeException("DB error inserting cheque: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Cheque> getChequesByStatus(String status) {
        List<Cheque> list = new ArrayList<>();
        String sql = "SELECT * FROM cheques WHERE status = ? ORDER BY id DESC";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Cheque> getAllCheques() {
        List<Cheque> list = new ArrayList<>();
        String sql = "SELECT * FROM cheques ORDER BY id DESC";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void updateStatus(int chequeId, String newStatus) {
        String sql = "UPDATE cheques SET status = ? WHERE id = ?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, chequeId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateStatusAndBatch(int chequeId, String newStatus, String batchId) {
        String sql = "UPDATE cheques SET status = ?, batch_id = ? WHERE id = ?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setString(2, batchId);
            ps.setInt(3, chequeId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Cheque mapRow(ResultSet rs) throws SQLException {
        Cheque c = new Cheque();
        c.setId(rs.getInt("id"));
        c.setDrawerName(rs.getString("drawer_name"));
        c.setBankName(rs.getString("bank_name"));
        c.setBranchName(rs.getString("branch_name"));
        c.setMicrCode(rs.getString("micr_code"));
        c.setChequeNumber(rs.getString("cheque_number"));
        c.setAccountNumber(rs.getString("account_number"));
        c.setAmountDigits(rs.getString("amount_digits"));
        c.setAmountWords(rs.getString("amount_words"));
        c.setChequeDate(rs.getString("cheque_date"));
        c.setPayeeName(rs.getString("payee_name"));
        c.setStatus(rs.getString("status"));
        c.setSubmittedDate(rs.getString("submitted_date"));
        c.setBatchId(rs.getString("batch_id"));
        return c;
    }
}