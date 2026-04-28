package com.cts.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cts.dao.BatchDao;
import com.cts.model.Batch;
import com.cts.util.DbConnection;

public class BatchDaoImpl implements BatchDao {

    @Override
    public void insertBatch(Batch batch) {
        String sql = "INSERT INTO batches (id, cheque_count, total_amount, status) VALUES (?, ?, ?, 'PENDING')";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, batch.getId());
            ps.setInt(2, batch.getChequeCount());
            ps.setString(3, batch.getTotalAmount());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Batch> getBatchesByStatus(String status) {
        List<Batch> list = new ArrayList<>();
        String sql = "SELECT * FROM batches WHERE status = ? ORDER BY id DESC";

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
    public List<Batch> getAllBatches() {
        List<Batch> list = new ArrayList<>();
        String sql = "SELECT * FROM batches ORDER BY id DESC";

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
    public void updateBatchStatus(String batchId, String newStatus) {
        String sql;

        // Set the correct date column based on new status
        if (newStatus.equals("APPROVED")) {
            sql = "UPDATE batches SET status = ?, approved_on = CURRENT_DATE WHERE id = ?";
        } else if (newStatus.equals("REJECTED")) {
            sql = "UPDATE batches SET status = ?, rejected_on = CURRENT_DATE WHERE id = ?";
        } else {
            sql = "UPDATE batches SET status = ? WHERE id = ?";
        }

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setString(2, batchId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Returns how many batches exist so we can generate the next batch ID
    @Override
    public int getNextBatchNumber() {
        String sql = "SELECT COUNT(*) FROM batches";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 1;
    }

    private Batch mapRow(ResultSet rs) throws SQLException {
        Batch b = new Batch();
        b.setId(rs.getString("id"));
        b.setChequeCount(rs.getInt("cheque_count"));
        b.setTotalAmount(rs.getString("total_amount"));
        b.setStatus(rs.getString("status"));
        b.setDateReceived(rs.getString("date_received"));
        b.setApprovedOn(rs.getString("approved_on"));
        b.setRejectedOn(rs.getString("rejected_on"));
        return b;
    }
}