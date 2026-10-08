package com.vitalfit.controller;

import com.vitalfit.model.User;
import com.vitalfit.service.LeaderboardService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/leaderboard")
public class LeaderboardServlet extends HttpServlet {

    private LeaderboardService leaderboardService;

    @Override
    public void init() {
        leaderboardService = new LeaderboardService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            List<LeaderboardService.LeaderboardEntry> fullLeaderboard = leaderboardService.getLeaderboard();
            List<LeaderboardService.LeaderboardEntry> podium = leaderboardService.getPodium(fullLeaderboard);

            req.setAttribute("leaderboard", fullLeaderboard);
            req.setAttribute("podium", podium);
            req.getRequestDispatcher("/WEB-INF/views/leaderboard.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Error compiling leaderboard metrics: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/leaderboard.jsp").forward(req, resp);
        }
    }
}
