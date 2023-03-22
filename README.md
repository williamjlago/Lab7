# Lab7
COS285 Lab 7

(Will)
Tokenization: Break down the lyrics of each song into individual words (tokens).
Preprocessing: Convert all the tokens to lowercase, remove stop words (commonly occurring words like 'the', 'a', 'an', etc.), and remove any special characters like punctuation marks, etc.

(Meghan)
Compute TF: Compute the term frequency of each token in each song.

(Esther)
Compute IDF: Compute the inverse document frequency of each token by counting the number of documents (songs) in which the token appears.

(Liban)
Compute TF-IDF: Multiply the term frequency and inverse document frequency to get the TF-IDF score for each token in each song.
Query Processing: Tokenize, preprocess, and compute the TF-IDF score for each token in the query.
Ranking: Compute the ranking score of each song by summing up the TF-IDF scores of all the tokens in the query that appear in the song.
