import java.util.*;
import java.sql.*;
import java.lang.*;

public class Scoring {
   public static void editTeamScore(Connection conn, int id, double score) {
      Statement statement = null;
      ResultSet results = null;
      String query = "UPDATE Team SET score = score + " + score + " WHERE id = " + id;

      try {
         statement = conn.createStatement();
         statement.executeUpdate(query);

      } catch (SQLException sqlEx) {
         System.err.println("Error doing query: " + sqlEx);
         sqlEx.printStackTrace(System.err);
      } finally {
         try {
            if (results != null) {
               results.close();
               results = null;
            }

            if (statement != null) {
               statement.close();
               statement = null;
            }
         } catch (Exception ex) {
            System.err.println("Error closing query: " + ex);
            ex.printStackTrace(System.err);
         }     
      }
   }

   public static void runWeek(Connection conn, int week, int numPlayers) {
      for (int i = 1; i < numPlayers + 1; i++) {
         getTeamScore(conn, week, i);
      }
   }

   public static String getLoserTeam(Connection conn) {
      Statement statement = null;
      ResultSet results = null;
      String str = "";
      String query = "SELECT id, name, score FROM Team WHERE elim = false ORDER BY score LIMIT 1" ;
      try {
         statement = conn.createStatement();
         results = statement.executeQuery(query);

         while (results.next()) {
            String teamID = results.getString(1);
            String teamName = results.getString(2);
            String teamScore = results.getString(3);

            str += String.format("%s %s %s", teamID, teamName, teamScore);
         }
      } catch (SQLException sqlEx) {
         System.err.println("Error doing query: " + sqlEx);
         sqlEx.printStackTrace(System.err);
      } finally {
         try {
            if (results != null) {
               results.close();
               results = null;
            }

            if (statement != null) {
               statement.close();
               statement = null;
            }
         } catch (Exception ex) {
            System.err.println("Error closing query: " + ex);
            ex.printStackTrace(System.err);
         }     
      }
      return str;
   }

   public static ArrayList<String> getRoundStats(Connection conn, boolean getALL) {
      Statement statement = null;
      ResultSet results = null;
      ArrayList<String> allTeams = new ArrayList<String>();
      String query = "";
      if (getALL == false) {
         query += "SELECT name, score FROM Team WHERE elim = false ORDER BY score DESC" ;
      }
      else {
         query += "SELECT name, score FROM Team ORDER BY score DESC";
      }
      try {
         statement = conn.createStatement();
         results = statement.executeQuery(query);

         while (results.next()) {
            String teamName = results.getString(1);
            String teamScore = results.getString(2);

            allTeams.add(String.format("%s|%s", teamName, teamScore));
         }
      } catch (SQLException sqlEx) {
         System.err.println("Error doing query: " + sqlEx);
         sqlEx.printStackTrace(System.err);
      } finally {
         try {
            if (results != null) {
               results.close();
               results = null;
            }

            if (statement != null) {
               statement.close();
               statement = null;
            }
         } catch (Exception ex) {
            System.err.println("Error closing query: " + ex);
            ex.printStackTrace(System.err);
         }     
      }
      return allTeams;
   }


   public static double getTeamScore(Connection conn, int week, int id) {
      Statement statement = null;
      ResultSet results = null;

      String qbQuery = "SELECT QB FROM Team WHERE id = " + id + " AND elim = false";
      String rb1Query = "SELECT RB1 FROM Team WHERE id = " + id + " AND elim = false";
      String rb2Query = "SELECT RB2 FROM Team WHERE id = " + id + " AND elim = false";
      String wr1Query = "SELECT WR1 FROM Team WHERE id = " + id + " AND elim = false";
      String wr2Query = "SELECT WR2 FROM Team WHERE id = " + id + " AND elim = false";
      String wr3Query = "SELECT WR3 FROM Team WHERE id = " + id + " AND elim = false";
      String teQuery = "SELECT TE FROM Team WHERE id = " + id + " AND elim = false";

      String qbStr = "";
      String rb1Str = "";
      String rb2Str = "";
      String wr1Str = "";
      String wr2Str = "";
      String wr3Str = "";
      String teStr = "";

      double teamScore = 0;
      try {
         statement = conn.createStatement();
         results = statement.executeQuery(qbQuery);
         if(results.next())
            qbStr = results.getString(1);

         statement = conn.createStatement();
         results = statement.executeQuery(rb1Query);
         if(results.next())
            rb1Str = results.getString(1);

         statement = conn.createStatement();
         results = statement.executeQuery(rb2Query);
         if(results.next())
            rb2Str = results.getString(1);

         statement = conn.createStatement();
         results = statement.executeQuery(wr1Query);
         if(results.next())
            wr1Str = results.getString(1);

         statement = conn.createStatement();
         results = statement.executeQuery(wr2Query);
         if(results.next())
            wr2Str = results.getString(1);

         statement = conn.createStatement();
         results = statement.executeQuery(wr3Query);
         if(results.next())
            wr3Str = results.getString(1);

         statement = conn.createStatement();
         results = statement.executeQuery(teQuery);
         if(results.next())
            teStr = results.getString(1);


         teamScore = getPlayerScore(conn, week, qbStr) + getPlayerScore(conn, week, rb1Str)
                     + getPlayerScore(conn, week, rb2Str) + getPlayerScore(conn, week, wr1Str)
                     + getPlayerScore(conn, week, wr2Str) + getPlayerScore(conn, week, wr3Str)
                     + getPlayerScore(conn, week, teStr);

         editTeamScore(conn, id, teamScore);

      } catch (SQLException sqlEx) {
         System.err.println("Error doing query: " + sqlEx);
         sqlEx.printStackTrace(System.err);
      } finally {
         try {
            if (results != null) {
               results.close();
               results = null;
            }

            if (statement != null) {
               statement.close();
               statement = null;
            }
         } catch (Exception ex) {
            System.err.println("Error closing query: " + ex);
            ex.printStackTrace(System.err);
         }     
      }

      return teamScore;
   }

   public static double getPlayerScore(Connection conn, int week, String player) {
      double score = qbScore(conn, week, player) + rbScore(conn, week, player) + recScore(conn, week, player);
		return score;      
   }

   public static void eliminateTeam(Connection conn, int teamId) {
      Statement statement = null;
      ResultSet results = null;
      String query = "UPDATE Team SET elim = true where id = " + teamId;
        try {
         statement = conn.createStatement();
         statement.executeUpdate(query);

      } catch (SQLException sqlEx) {
         System.err.println("Error doing query: " + sqlEx);
         sqlEx.printStackTrace(System.err);
      } finally {
         try {
            if (results != null) {
               results.close();
               results = null;
            }

            if (statement != null) {
               statement.close();
               statement = null;
            }
         } catch (Exception ex) {
            System.err.println("Error closing query: " + ex);
            ex.printStackTrace(System.err);
         }     
      }
   }


   public static void resetTeams(Connection conn) {
      Statement statement = null;
      ResultSet results = null;
      String query = "UPDATE Team SET score = 0, elim = false ,QB = '', RB1 = '', RB2 = '', WR1 = '', WR2 = '', WR3 = '', TE = ''";
      String query2 = "UPDATE Team SET name = '' WHERE id = 1";
        try {
         statement = conn.createStatement();
         statement.executeUpdate(query);
         statement.executeUpdate(query2);

      } catch (SQLException sqlEx) {
         System.err.println("Error doing query: " + sqlEx);
         sqlEx.printStackTrace(System.err);
      } finally {
         try {
            if (results != null) {
               results.close();
               results = null;
            }

            if (statement != null) {
               statement.close();
               statement = null;
            }
         } catch (Exception ex) {
            System.err.println("Error closing query: " + ex);
            ex.printStackTrace(System.err);
         }     
      }
   }

   public static double qbScore(Connection conn, int week, String player) {
   	Statement statement = null;
      ResultSet results = null;
      double score = 0;
      String scoreStr = "";

      String query = "SELECT COALESCE(SUM(yds) / 25 + X.tds, 0) AS pts FROM Play P JOIN Game G ON G.gid = P.gid JOIN " + 
                     "Pass S ON P.pid = S.pid JOIN Player L ON L.player = S.psr JOIN (SELECT COUNT(*) * " + 
                     "4 AS tds, L.player FROM Play P JOIN Game G ON G.gid = P.gid JOIN Pass S ON S.pid = " + 
                     "P.pid JOIN Player L ON S.psr = L.player JOIN Td T ON P.pid = T.pid WHERE wk = " + week + 
                     " AND P.type = 'PASS' AND L.player = '" + player + "') X ON X.player = L.player WHERE wk = " + 
                     week + " AND P.type = 'PASS' AND L.player = '" + player + "'";

      try {
         statement = conn.createStatement();
         results = statement.executeQuery(query);
         if(results.next())
            scoreStr = results.getString(1);
         if (scoreStr == null)
            scoreStr = "0";

         score = Double.parseDouble(scoreStr);

      } catch (SQLException sqlEx) {
         System.err.println("Error doing query: " + sqlEx);
         sqlEx.printStackTrace(System.err);
      } finally {
         try {
            if (results != null) {
               results.close();
               results = null;
            }

            if (statement != null) {
               statement.close();
               statement = null;
            }
         } catch (Exception ex) {
            System.err.println("Error closing query: " + ex);
            ex.printStackTrace(System.err);
         }     
      }
      return score;
   }

   public static double rbScore(Connection conn, int week, String player) {
   	Statement statement = null;
      ResultSet results = null;
      double score = 0;
      String scoreStr = "";

      String query = "SELECT COALESCE((SUM(R.yds) / 10) + X.tds, 0) AS pts FROM Play P JOIN Game G ON G.gid = P.gid " + 
   						"JOIN Rush R ON R.pid = P.pid JOIN Player L ON R.bc = L.player JOIN (SELECT COUNT(*) * 6 AS tds, " +
         				"L.player AS player FROM Play P JOIN Game G ON G.gid = P.gid JOIN Rush R ON R.pid = P.pid JOIN Player L ON R.bc = L.player " +
      	   			"JOIN Td T ON P.pid = T.pid WHERE wk = " + week + " AND P.type = 'RUSH' AND L.player = '" + player + "') X ON X.player = L.player " +
							"WHERE wk = " + week + " AND P.type = 'RUSH' AND L.player = '" + player + "'";
      try {
         statement = conn.createStatement();
         results = statement.executeQuery(query);
         if(results.next())
            scoreStr = results.getString(1);
         if (scoreStr == null)
            scoreStr = "0";
         score = Double.parseDouble(scoreStr);
         
      } catch (SQLException sqlEx) {
         System.err.println("Error doing query: " + sqlEx);
         sqlEx.printStackTrace(System.err);
      } finally {
         try {
            if (results != null) {
               results.close();
               results = null;
            }

            if (statement != null) {
               statement.close();
               statement = null;
            }
         } catch (Exception ex) {
            System.err.println("Error closing query: " + ex);
            ex.printStackTrace(System.err);
         }     
      }
      return score;
   }

   public static double recScore(Connection conn, int week, String player) {
   	Statement statement = null;
      ResultSet results = null;
      double score = 0;
      String scoreStr = "";

      String query = "SELECT COALESCE((SUM(S.yds) / 10) + X.tds, 0) AS pts FROM Play P JOIN Game G ON G.gid = P.gid " +
   						"JOIN Pass S ON P.pid = S.pid JOIN Player L ON S.trg = L.player JOIN (SELECT COUNT(*) * 6 AS tds, " +
         				"L.player FROM Play P JOIN Game G ON G.gid = P.gid JOIN Pass S ON S.pid = P.pid JOIN Player L ON S.trg = L.player " +
         				"JOIN Td T ON P.pid = T.pid WHERE wk = " + week + " AND P.type = 'PASS' AND L.player = '" + player + "') X ON X.player = L.player " + 
							"WHERE wk = " + week + " AND P.type = 'PASS' AND L.player = '" + player + "'";
      try {
         statement = conn.createStatement();
         results = statement.executeQuery(query);
         if(results.next())
            scoreStr = results.getString(1);
         if (scoreStr == null)
            scoreStr = "0";
         score = Double.parseDouble(scoreStr);
         
      } catch (SQLException sqlEx) {
         System.err.println("Error doing query: " + sqlEx);
         sqlEx.printStackTrace(System.err);
      } finally {
         try {
            if (results != null) {
               results.close();
               results = null;
            }

            if (statement != null) {
               statement.close();
               statement = null;
            }
         } catch (Exception ex) {
            System.err.println("Error closing query: " + ex);
            ex.printStackTrace(System.err);
         }     
      }
      return score;
   }

   public static void addTeamToLeaderboard(Connection conn, int teamId) {
      PreparedStatement prep = null;
      ResultSet results = null;
      String name = getTeamName(conn, teamId);
      Double score = queryTeamScore(conn, teamId);
      String qb = getPostionFromTeam(conn, teamId, "QB");
      String rb1 = getPostionFromTeam(conn, teamId, "RB1");
      String rb2 = getPostionFromTeam(conn, teamId, "RB2");
      String wr1 = getPostionFromTeam(conn, teamId, "WR1");
      String wr2 = getPostionFromTeam(conn, teamId, "WR2");
      String wr3 = getPostionFromTeam(conn, teamId, "WR3");
      String te = getPostionFromTeam(conn, teamId, "TE");
      String query = "INSERT INTO Leaderboard (name, score, QB, RB1, RB2, WR1, WR2, WR3, TE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

      try {
         prep = conn.prepareStatement(query);
         prep.setString(1, name);
         prep.setDouble(2, score);
         prep.setString(3, qb);
         prep.setString(4, rb1);
         prep.setString(5, rb2);
         prep.setString(6, wr1);
         prep.setString(7, wr2);
         prep.setString(8, wr3);
         prep.setString(9, te);


         prep.executeUpdate();

      } catch (SQLException sqlEx) {
         System.err.println("Error doing query: " + sqlEx);
         sqlEx.printStackTrace(System.err);
      } finally {
         try {
            if (results != null) {
               results.close();
               results = null;
            }

            if (prep != null) {
               prep.close();
               prep = null;
            }
         } catch (Exception ex) {
            System.err.println("Error closing query: " + ex);
            ex.printStackTrace(System.err);
         }     
      }
   }

   private static String getPostionFromTeam(Connection conn, int teamId, String pos) {
      Statement statement = null;
      ResultSet results = null;
      String query = "SELECT " + pos + " FROM Team WHERE id = " + teamId;
      String player = "";

      try {
         statement = conn.createStatement();
         results = statement.executeQuery(query);
         if(results.next())
            player = results.getString(1);

      } catch (SQLException sqlEx) {
         System.err.println("Error doing query: " + sqlEx);
         sqlEx.printStackTrace(System.err);
      } finally {
         try {
            if (results != null) {
               results.close();
               results = null;
            }

            if (statement != null) {
               statement.close();
               statement = null;
            }
         } catch (Exception ex) {
            System.err.println("Error closing query: " + ex);
            ex.printStackTrace(System.err);
         }     
      }
      return player;
   }

   private static String getTeamName(Connection conn, int teamId) {
      Statement statement = null;
      ResultSet results = null;
      String query = "SELECT name FROM Team WHERE id = " + teamId;
      String team = "";

      try {
         statement = conn.createStatement();
         results = statement.executeQuery(query);
         if(results.next())
            team = results.getString(1);

      } catch (SQLException sqlEx) {
         System.err.println("Error doing query: " + sqlEx);
         sqlEx.printStackTrace(System.err);
      } finally {
         try {
            if (results != null) {
               results.close();
               results = null;
            }

            if (statement != null) {
               statement.close();
               statement = null;
            }
         } catch (Exception ex) {
            System.err.println("Error closing query: " + ex);
            ex.printStackTrace(System.err);
         }     
      }
      return team;
   }

   public static double queryTeamScore(Connection conn, int teamId) {
      Statement statement = null;
      ResultSet results = null;
      double score = 0;
      String scoreStr = "";

      String query = "SELECT score FROM Team WHERE id = " + teamId;
      try {
         statement = conn.createStatement();
         results = statement.executeQuery(query);
         if(results.next())
            scoreStr = results.getString(1);
         if (scoreStr == null)
            scoreStr = "0";
         score = Double.parseDouble(scoreStr);
         
      } catch (SQLException sqlEx) {
         System.err.println("Error doing query: " + sqlEx);
         sqlEx.printStackTrace(System.err);
      } finally {
         try {
            if (results != null) {
               results.close();
               results = null;
            }

            if (statement != null) {
               statement.close();
               statement = null;
            }
         } catch (Exception ex) {
            System.err.println("Error closing query: " + ex);
            ex.printStackTrace(System.err);
         }     
      }
      return score;
   }

   public static void resetLeaderboard(Connection conn) {
      Statement statement = null;
      ResultSet results = null;
      String query = "DELETE FROM Leaderboard";
        try {
         statement = conn.createStatement();
         statement.executeUpdate(query);

      } catch (SQLException sqlEx) {
         System.err.println("Error doing query: " + sqlEx);
         sqlEx.printStackTrace(System.err);
      } finally {
         try {
            if (results != null) {
               results.close();
               results = null;
            }

            if (statement != null) {
               statement.close();
               statement = null;
            }
         } catch (Exception ex) {
            System.err.println("Error closing query: " + ex);
            ex.printStackTrace(System.err);
         }     
      }
   }

   public static ArrayList<String> getLeaderboard(Connection conn) {
      Statement statement = null;
      ResultSet results = null;
      String res = "";
      ArrayList<String> leaders = new ArrayList<String>();

      String query = "SELECT * FROM Leaderboard ORDER BY score DESC";
      try {
         statement = conn.createStatement();
         results = statement.executeQuery(query);

         while (results.next()) {
            leaders.add(results.getString(1) + "|" + results.getString(2) + "|" + getPlayerFromId(conn, results.getString(3)) + 
                        "|" + getPlayerFromId(conn, results.getString(4)) + "|" + getPlayerFromId(conn, results.getString(5)) + 
                        "|" + getPlayerFromId(conn, results.getString(6)) + "|" + getPlayerFromId(conn, results.getString(7)) + 
                        "|" + getPlayerFromId(conn, results.getString(8)) + "|" + getPlayerFromId(conn, results.getString(9)));
         }
         
      } catch (SQLException sqlEx) {
         System.err.println("Error doing query: " + sqlEx);
         sqlEx.printStackTrace(System.err);
      } finally {
         try {
            if (results != null) {
               results.close();
               results = null;
            }

            if (statement != null) {
               statement.close();
               statement = null;
            }
         } catch (Exception ex) {
            System.err.println("Error closing query: " + ex);
            ex.printStackTrace(System.err);
         }     
      }
      return leaders;
   }

   public static String getPlayerFromId(Connection conn, String id) {
      Statement statement = null;
      ResultSet results = null;
      String query = "SELECT fname, lname FROM Player WHERE player = '" + id + "'";
      String name = "";

      try {
         statement = conn.createStatement();
         results = statement.executeQuery(query);
         if(results.next())
            name = results.getString(1) + " " +results.getString(2);

         
      } catch (SQLException sqlEx) {
         System.err.println("Error doing query: " + sqlEx);
         sqlEx.printStackTrace(System.err);
      } finally {
         try {
            if (results != null) {
               results.close();
               results = null;
            }

            if (statement != null) {
               statement.close();
               statement = null;
            }
         } catch (Exception ex) {
            System.err.println("Error closing query: " + ex);
            ex.printStackTrace(System.err);
         }     
      }

      return name;
   }
}
