package com.pm.Project_Management_Server.exceptions;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<String> handleUsernameExists(UsernameAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCreds(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error: " + ex.getMessage());
    }

    @ExceptionHandler(RateCardNotFoundException.class)
    public ResponseEntity<String> handleRateCardNotFound(RateCardNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(RateCardAlreadyExistsException.class)
    public ResponseEntity<String> handleRateCardExists(RateCardAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
    @ExceptionHandler(DuplicateRateLevelException.class)
    public ResponseEntity<String> handleDuplicateRate(DuplicateRateLevelException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(HighlightNotFoundException.class)
    public ResponseEntity<String> handleHighlightNotFound(HighlightNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<String> handleProjectNotFound(ProjectNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidHighlightDescriptionException.class)
    public ResponseEntity<String> handleInvalidDesc(InvalidHighlightDescriptionException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(IssueNotFoundException.class)
    public ResponseEntity<String> handleIssueNotFound(IssueNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidIssueSeverityException.class)
    public ResponseEntity<String> handleInvalidSeverity(InvalidIssueSeverityException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(OpenPositionNotFoundException.class)
    public ResponseEntity<String> handleOpenPositionNotFound(OpenPositionNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidResourceLevelException.class)
    public ResponseEntity<String> handleInvalidLevel(InvalidResourceLevelException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(ProjectLeadNotFoundException.class)
    public ResponseEntity<String> handleProjectLeadNotFound(ProjectLeadNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }


    @ExceptionHandler(NoLeadAssignedException.class)
    public ResponseEntity<String> handleNoLeadAssigned(NoLeadAssignedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<String> handleClientNotFound(ClientNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ContactPersonNotFoundException.class)
    public ResponseEntity<String> handleContactPersonNotFound(ContactPersonNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ProjectForLeadNotFoundException.class)
    public ResponseEntity<String> handleProjectForLeadNotFound(ProjectForLeadNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(RequirementNotFoundException.class)
    public ResponseEntity<String> handleRequirementNotFound(RequirementNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(NoSearchResultsFoundException.class)
    public ResponseEntity<String> handleNoResultsFound(NoSearchResultsFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidUserTypeException.class)
    public ResponseEntity<String> handleInvalidUserType(InvalidUserTypeException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }






}
