//Assignment 1
//Reading Email-Addresses out of a corrupted database file "corrupteddb"
//Jonas Schaefer 1944365

//code apart from the emailAddressFinder provided by the lecturer
//any code other than Jonas Schäfer's taken out due to publication restriction

public ArrayList<String> findEmailAddresses(String input) {
    
    ArrayList<String> list = new ArrayList<String>();

    StringBuilder input2 = new StringBuilder(input); 
    
    //used instead of String input as it enables easier editing of the input String

    int periodCounter; //counts period signs for every email for both domain-part and local-part

    boolean domainPartIsValid;
    boolean localPartIsValid;

    int emailBegin; //remembers begin of currently tested emailaddress for list-adding purposes
    int emailEnd; //remembers end of currently tested emailaddress for list-adding purposes

    for (int i = 0; i < input2.length(); i++) {

        if (input2.charAt(i) == '@') { //@-sign starting point for email verification

            //setting parameters to default values
            periodCounter = 0;

            domainPartIsValid = false;
            localPartIsValid = false;

            emailBegin = 0;
            emailEnd  = 0;

            
            for (int j = 1; input2.charAt(i + j) == '.' || Character.isLowerCase(input2.charAt(i + j)); j++) {
                //check after @-sign if domain part is valid

                if (input2.charAt(i + j) == '.') {
                    if (j == 1) { //check if first sign is a period sign
                        break; //invalid
                    } else {
                        periodCounter++;
                    }
                }

                if (periodCounter > 0) { //checks for top-end domains, if domain exists:

                    //set to avoid unnecessary calling of charAt-method
                    char topEndDomain0 = input2.charAt(i + j);
                    char topEndDomain1 = input2.charAt(i + j + 1);
                    char topEndDomain2 = input2.charAt(i + j + 2);
                    char topEndDomain3 = input2.charAt(i + j + 3);

                    //check for at least two lowercase letters after the period sign to optimize runtime
                    if (topEndDomain0 == '.' && Character.isLowerCase(topEndDomain1) && Character.isLowerCase(topEndDomain2)) {

                        if (topEndDomain1 == 'n' && topEndDomain2 == 'e' && topEndDomain3 == 't') {
                            domainPartIsValid = true;
                            emailEnd = i + j + 3;
                        } else {
                            if (topEndDomain1 == 'c' && topEndDomain2 == 'o' && topEndDomain3 == 'm') {
                                domainPartIsValid = true;
                                emailEnd = i + j + 3;
                            } else {
                                if (topEndDomain1 == 'u' && topEndDomain2 == 'k') {
                                    domainPartIsValid = true;
                                    emailEnd = i + j + 2;
                                } else {
                                    if (topEndDomain1 == 'd' && topEndDomain2 == 'e') {
                                        domainPartIsValid = true;
                                        emailEnd = i + j + 2;
                                    } else {
                                        if (topEndDomain1 == 'j' && topEndDomain2 == 'p') {
                                            domainPartIsValid = true;
                                            emailEnd = i + j + 2;
                                        } else {
                                            if (topEndDomain1 == 'r' && topEndDomain2 == 'o') {
                                                domainPartIsValid = true;
                                                emailEnd = i + j + 2;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                }

            }

            periodCounter = 0; //reset for re-use

            if (domainPartIsValid == true) { //checks local part

                for (int k = 1; i - k >= 0 && (input2.charAt(i - k) == '.' || input2.charAt(i - k) == '_' || Character.isLetterOrDigit(input2.charAt(i - k))); k++) {
                    
                    if (k == 1 && input2.charAt(i - k) == '.') { //check if first sign before @-sign is a period sign
                        break; //invalid
                    }

                    emailBegin = i - k;
                    localPartIsValid = true; //at least one valid character in front of the @-sign

                    if (input2.charAt(i - k) == '.') {
                        periodCounter++;

                        if (periodCounter > 1) {
                            break; //invalid
                        }
                    }
                    
                }
            }

            if (localPartIsValid == true) { //localPartIsValid can only be true if also domainPartIsValid == true
                
                if (input2.charAt(emailBegin) == '.') { //check for local parts beginning with a period sign
                    if (input2.charAt(emailBegin + 1) == '.') {
                        emailBegin += 2; //sets emailbegin as the following sign
                    } else {
                        emailBegin += 1;
                    }
                }

                int countlist = 0; 

                list.add(countlist, input2.substring(emailBegin, emailEnd + 1));

                input2.setCharAt(emailEnd, ' ');
                //sets last character of the email address to an unvalid character to 
                //avoid overlap with possible following emails

                countlist++;
            }

        } 
    }

    return list; //end
}

}