//Copyright 2022, Ty Greve
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.

/**
 * This class handles the creation of QR code objects
 */
package com.example.qr_scape;

public class Comment {
    private String qrInstance;
    private String commentText;
    private String timestamp;
    private String username;

    public Comment() {
    }

    /**
     * This method is the constructor method which creates an object
     * of type Comment
     * @param qrInstance
     * @param commentText
     * @param timestamp
     * @param username
     */
    public Comment(String qrInstance, String commentText, String timestamp,  String username) {
        this.qrInstance = qrInstance;
        this.commentText = commentText;
        this.timestamp = timestamp;
        this.username = username;
    }
    /**
     * This method gets the qrInstance of the Comment object
     * @return qrInstance
     * Returns the qrInstance of the Comment object
     */
    public String getQrInstance() {
        return qrInstance;
    }
    /**
     * This method sets the qrInstance of the comment object
     * @param qrInstance
     */
    public void setQrInstance(String qrInstance) {
        this.qrInstance = qrInstance;
    }
    /**
     * This method gets the commentText of the Comment object
     * @return commentText
     * Returns the commentText of the Comment object
     */
    public String getCommentText() {
        return commentText;
    }
    /**
     * This method sets the commentText of the comment object
     * @param commentText
     */
    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
    /**
     * This method gets the timestamp of the Comment object
     * @return timestamp
     * Returns the timestamp of the Comment object
     */
    public String getTimestamp() {
        return timestamp;
    }
    /**
     * This method sets the timestamp of the comment object
     * @param timestamp
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    /**
     * This method gets the username of the Comment object
     * @return username
     * Returns the username of the Comment object
     */
    public String getUsername() {
        return username;
    }
    /**
     * This method sets the username of the comment object
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
