    package main.java.common;

    import java.util.List;

    public interface Environment {
        /**
         * Adds a robot to its position
         *
         * @param robot The robot to be added
         * @return true if the operation is successful, false otherwise
         */
        boolean addRobot(Robot robot);

        /**
         * Creates an obstacle at the specified position
         *
         * @param row The row of the position
         * @param col The column of the position
         * @return true if the operation is successful, false otherwise
         */
        boolean createObstacleAt(int row, int col);

        /**
         * Removes a robot from the environment.
         * @param robot The robot to be removed.
         * @return true if the operation is successful, false otherwise.
         */
        boolean removeRobot(Robot robot);

        /**
         * Removes an obstacle at the specified position
         *
         * @param row The row of the position
         * @param col The column of the position
         * @return true if the operation is successful, false otherwise
         */
        boolean removeObstacleAt(int row, int col);

        /**
         * Checks if there is an obstacle at the specified row and column
         *
         * @param row The row to check
         * @param col The column to check
         * @return true if there is an obstacle at the specified position, false otherwise
         */
        boolean obstacleAt(int row, int col);

        /**
         * Checks if there is a robot at the specified position
         *
         * @param p The position to check
         * @return true if there is a robot at the specified position, false otherwise
         */
        boolean robotAt(Position p);

        /**
         * Checks if a given position is within the environment
         *
         * @param pos The position to check
         * @return true if the position is within the range of the environment, false otherwise
         */
        boolean containsPosition(Position pos);

        /**
         * Checks if there is an obstacle at the specified position
         *
         * @param p The position to check
         * @return true if there is an obstacle at the specified position, false otherwise
         */
        boolean obstacleAt(Position p);

        /**
         * Method to get the number of rows in the environment
         *
         * @return The number of rows in the environment
         */
        int getRows();

        /**
         * Method to get the number of columns in the environment
         *
         * @return The number of columns in the environment
         */
        int getCols();

        /**
         * Method to get the list of robots in the environment
         *
         * @return The list of robots in the environment
         */
        List<Robot> getRobots();

        /**
         * Method to get the list of obstacles in the environment
         *
         * @return The list of obstacles in the environment
         */
        List<Obstacle> getObstacles();

        /**
         * Method to clear all robots from the environment
         */
        public void clearRobots();

        /**
         * Method to clear all obstacles from the environment
         */
        public void clearObstacles();
    }
