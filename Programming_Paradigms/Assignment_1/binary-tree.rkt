#lang racket

(require racket/include)
(include "binary-tree-test.rkt")

;; TASK pregatitor
(define empty-tree
 '())

(define init-node 
  (λ (value) (list '() value '() )
  ))

(define make-node
  (λ (left right value) (list left value right)))

(define is-leaf?
  (λ (node)
    (if (equal? node empty-tree)
        #f
        (and (is-empty? (get-left node)) (is-empty? (get-right node)) (not (null? (get-value node))) ))
  ))

(define get-value
  (λ (node)
    (if (null? node)
        null
        (if (null? (cdr node))
            null
            (car (cdr node)))))
  )

(define get-left
  (λ (node)
    (if (null? node)
        empty-tree
        (car node))
  ))

(define get-right
  (λ (node)
    (if (null? node)
        empty-tree
        (if (null? (cdr node))
            empty-tree
            (car(cdr (cdr node)))))
  ))

(define is-node?
  (λ (node)
    (if (equal? node empty-tree)
        #t
        (and (list? (get-left node)) (list? (get-right node)) (not (null? (get-value node)))  ))))

(define is-empty?
  (λ (tree)
        (equal? empty-tree tree)
        ))

(define has-left?
  (λ (tree)
    (not(null? (get-left tree)))))

(define has-right?
  (λ (tree) (not(null? (get-right tree))))
  )

(define minimum
  (λ (tree)
    (if (null? (get-left tree))
        (get-value tree)
        (minimum (get-left tree))
  )))

(define maximum
  (λ (tree)
    (if (null? (get-right tree))
        (get-value tree)
        (maximum (get-right tree))
  )))

(define height
  (λ (tree)
    (if (null? tree)
        0
        (+ 1 (max (height (get-left tree)) (height (get-right tree))))
        ))
  )

(define inorder
  (λ (tree)
    (if (null? tree)
        '()
        (append (inorder (get-left tree)) (list (get-value tree)) (inorder (get-right tree)))
  )))

(define preorder
  (λ (tree)
    (if (null? tree)
        '()
        (append (list (get-value tree)) (inorder (get-left tree)) (inorder (get-right tree))))
  ))

(define postorder
  (λ (tree)
    (if (null? tree)
        '()
        (append (inorder (get-left tree)) (inorder (get-right tree)) (list (get-value tree)))
    )
  ))

(define successor
  (λ (tree value)
    (successor2 tree tree value)))
(define successor2
  (λ(parentTree tree value)
    (if (= value (get-value tree))
        (if (has-right? tree)
            (minimum (get-right tree))
            (find-right-successor parentTree value 0)
            )
        (if (< value (get-value tree))
            (successor2 parentTree (get-left tree) value)
            (successor2 parentTree (get-right tree) value))
        )))
(define (successor-help parentTree tree value)
  (if (and (not (has-left? tree)) (not (has-right? tree)))
      (get-value tree)
      (if (and (is-leaf? (get-left tree)) (equal? value (get-value (get-left tree))))
          (get-value tree)
          (if (and (not (has-right? (get-right tree))) (equal? value (get-value (get-right tree))))
              (find-right-successor parentTree value 0)
              (if (< value (get-value tree))
                  (if (has-left? tree)
                      (successor-help parentTree (get-left tree) value)
                      (successor-help parentTree (get-right tree) value))
                  (if (has-right? tree)
                      (successor-help parentTree (get-right tree) value)
                      (successor-help parentTree (get-left tree) value)))))))

(define (find-right-successor tree value result)
  (if (= value (get-value tree))
      result
      (if (< value (get-value tree))
          (find-right-successor (get-left tree) value (get-value tree))
          (find-right-successor (get-right tree) value result))))
(define predecessor
  (λ (tree value)
    (predecessor-help tree tree value)
  ))
(define (predecessor-help parentTree tree value)
  (if (= value (get-value tree))
      (if (has-left? tree)
         (maximum (get-left tree))
         (find-upper-pred parentTree value 0))
      (if (< value (get-value tree))
          (predecessor-help parentTree (get-left tree) value)
          (predecessor-help parentTree (get-right tree) value)
          )))
(define (find-upper-pred tree value predec)
  (if (= value (get-value tree))
      predec
      (if (< value (get-value tree))
          (find-upper-pred (get-left tree) value predec)
          (find-upper-pred (get-right tree) value (get-value tree)))
      ))
  
  
(define binary-search-tree '((((() 1 ()) 2 ()) 3 ((() 5 ()) 6 ((() 7 ()) 8 ()))) 9 ((() 11 ()) 12 ((() 13 ()) 15 (() 21 ())))))

;;Task 1

(define insert
  (λ(tree value)
    (cond
      [(null? tree) (list '() value '())]
      [(< value (get-value tree))  (balance-help (make-node (insert (get-left tree) value) (get-right tree) (get-value tree)))]
      [(> value (get-value tree))  (balance-help (make-node (get-left tree) (insert (get-right tree) value) (get-value tree)))]
      [else tree])))
                               
;(define balance
 ; (λ (tree)
  ;))
(define balance-help
  (λ(tree)
    (cond
      [(and (> (- (height (get-left tree)) (height (get-right tree))) 1) (> (height (get-left (get-left tree))) (height (get-right (get-left tree))))) (rightRotate tree)]
      [(and (< (- (height (get-left tree)) (height (get-right tree))) -1) (< (height (get-left (get-right tree))) (height (get-right (get-right tree))))) (leftRotate tree)]
      [(and (> (- (height (get-left tree)) (height (get-right tree))) 1) (< (height (get-left (get-left tree)))  (height (get-right (get-left tree))))) (rightRotate (make-node (leftRotate (get-left tree)) (get-right tree) (get-value tree)))]
      [(and (< (- (height (get-left tree)) (height (get-right tree))) -1) (< (height (get-right (get-right tree))) (height (get-left (get-right tree))))) (leftRotate (make-node (get-left tree) (rightRotate (get-right tree)) (get-value tree)))]
      [else tree]
      )))
(define leftRotate
  (λ(tree)
   (make-node (make-node (get-left tree) (get-left (get-right tree)) (get-value tree)) (get-right (get-right tree)) (get-value (get-right tree)))
    ))
(define rightRotate
  (λ(tree)
    (make-node (get-left (get-left tree)) (make-node (get-right (get-left tree)) (get-right tree) (get-value tree)) (get-value (get-left tree)))
    ))
                                                                                                                                    

(define union
  (λ (tree1 tree2)
    (foldl (λ(value tre) (if (not (contains tre value)) (insert tre value) tre)) tree1 (inorder tree2))
  ))

(define intersection
  (λ (tree1 tree2)
    (foldl (λ(value tree) (insert tree value)) empty-tree (intersection-list (inorder tree1) (inorder tree2)))
  ))
(define (intersection-list a b)
  (if (null? a)
      '()
      (if (member (car a) b)
          (cons (car a) (intersection-list (cdr a) b))
          (intersection-list (cdr a) b))))

(define complements
  (λ (tree1 tree2)
    (foldl (λ(value tree) (if (not (contains tree2 value)) (insert tree value) tree)) empty-tree (inorder tree1))
    )
  )

(define contains
  (λ (tree value)
    (cond
      [(null? tree) #f]
      [(= value (get-value tree)) #t]
      [(< value (get-value tree)) (contains (get-left tree) value)]
      [(> value (get-value tree)) (contains (get-right tree) value)]
  )))
(define curry-contains
  (λ(tree)
    (λ(value)
      (contains tree value))))

(define remove
  (λ (tree value)
    (remove-help tree tree value)))

(define remove-help
  (λ (parentTree tree value)
    (cond
      [(null? tree) tree]
      [(= value (get-value tree)) (if (is-leaf? tree)
                                      empty-tree
                                      (if (has-right? tree)
                                          (balance-help (make-node (remove (get-left tree) (successor tree value)) (remove (get-right tree) (successor tree value)) (successor tree value)))
                                          (balance-help (make-node (remove (get-left tree) (predecessor tree value)) (remove (get-right tree) (predecessor tree value)) (predecessor tree value))))
                                          )]
      [(< value (get-value tree)) (balance-help (make-node (remove-help parentTree (get-left tree) value) (get-right tree) (get-value tree)))]
      [(> value (get-value tree)) (balance-help (make-node (get-left tree) (remove-help parentTree (get-right tree) value) (get-value tree)))]
      )))

;;Task 2
(define k-subsets
  (λ (set k)
    (filter (λ(l)
              (= (length l) k)) (power-set (inorder set)))))
  

(define (permutations items)
  (if (null? items) '(())
      (apply append
             (map (lambda (element)
                    (map (lambda (permutation)
                           (cons element permutation))
                         (permutations (removeFromList items element))))
                  items))))
(define (removeFromList ls elem)
  (cond ((null? ls) '())
        ((equal? (car ls) elem) (removeFromList (cdr ls) elem))
        (else (cons (car ls) (removeFromList (cdr ls) elem)))))

(define (power-set set)
  (if (null? set) '(())
      (let ((power-set-of-rest (power-set (cdr set))))
        (append power-set-of-rest
                (map (lambda (subset) (cons (car set) subset))
                     power-set-of-rest)))))

(define (check-zigzag1 l counter)
  (if (null? (cdr l))
      #t
      (if (even? counter)
          (if (< (car l) (cadr l))
              (check-zigzag1 (cdr l) (+ counter 1))
              #f)
          (if (> (car l) (cadr l))
              (check-zigzag1 (cdr l) (+ counter 1))
              #f)
          )))
(define (check-zigzag2 l counter)
  (if (null? (cdr l))
      #t
      (if (even? counter)
          (if (> (car l) (cadr l))
              (check-zigzag2 (cdr l) (+ counter 1))
              #f)
          (if (< (car l) (cadr l))
              (check-zigzag2 (cdr l) (+ counter 1))
              #f)
          )))
(define (check-zigzag l)
  (xor (check-zigzag1 l 0) (check-zigzag2 l 0)))
              

(define zig-zag-subsets
  (λ (set)
    (filter check-zigzag (permutations (inorder set)))
  ))

;;BONUS

(define (parser exp)
  (if (number? exp)
      (init-node exp)
      (if (> (length exp) 1)
          (if (or (equal? (cadr exp) '*) (equal? (cadr exp) '/))
              (if (> (length exp) 3)
                  (make-node (make-node (parser (car exp)) (parser (caddr exp)) (cadr exp)) (parser (cddddr exp)) (cadddr exp))
                  (make-node (parser (car exp)) (parser (cddr exp)) (cadr exp)))
              (make-node (parser (car exp)) (parser (cddr exp)) (cadr exp)))
          (parser (car exp)))))
       

(define evaluate
  (λ (tree)
    (if (is-leaf? tree)
        (get-value tree)
        (cond
          [(equal? (get-value tree) '+) (+ (evaluate (get-left tree)) (evaluate (get-right tree)))]
          [(equal? (get-value tree) '*) (* (evaluate (get-left tree)) (evaluate (get-right tree)))]
          [(equal? (get-value tree) '/) (/ (evaluate (get-left tree)) (evaluate (get-right tree)))]
          [(equal? (get-value tree) '-) (- (evaluate (get-left tree)) (evaluate (get-right tree)))]
          )
        )))
          

;; SECȚIUNE DE TESTARE - NU modificați această linie!
;; ATENȚIE! Pentru a primi punctaj pe temă, NU modificați această secțiune!
;;
;; CHECK - TASK 0 - NU modificați această linie!
;; ATENȚIE! NU modificați această secțiune
(Task 0 : 30 puncte) ;;check-exp
(define functions (list is-node? is-leaf? is-empty? get-value make-node get-right get-left inorder height insert empty-tree)) ;;check-exp
(define tree0 binary-search-tree) ;;check-exp
(check-exp-part 'is-node .037 (is-node? tree0) #t)
(check-exp-part 'is-leaf?1 .037 (is-leaf? tree0) #f)
(check-exp-part 'is-leaf?2 .037 (is-leaf? (init-node 8)) #t)
(check-exp-part 'is-empty?1 .037 (is-empty? tree0) #f)
(check-exp-part 'is-empty?2 .037 (is-empty? empty-tree) #t)
(check-exp-part 'get-value1 .037 (get-value tree0) 9)
(check-exp-part 'get-value2 .037 (get-value (get-left tree0)) 3)
(check-exp-part 'get-value3 .037 (get-value (get-right tree0)) 12)
(check-exp-part 'make-node .037 (make-node (get-left tree0) (get-right tree0) (get-value tree0)) binary-search-tree)
(check-exp-part 'minimum .0833 (minimum tree0) 1)
(check-exp-part 'maximum .0833 (maximum tree0) 21)
(check-exp-part 'height1 .0833 (height tree0) 5)
(check-exp-part 'height2 .0833 (height (get-left (get-left tree0))) 2)
(check-exp-part 'successor1 .055 (successor tree0 9) 11)
(check-exp-part 'successor2 .055 (successor tree0 5) 6)
(check-exp-part 'successor3 .055 (successor tree0 8) 9)
(check-exp-part 'predecessor1 .056 (predecessor tree0 9) 8)
(check-exp-part 'predecessor2 .056 (predecessor tree0 5) 3)
(check-exp-part 'predecessor3 .057 (predecessor tree0 12) 11)
;; SFÂRȘIT CHECK - TASK 0 - NU modificați această linie!
;;
;; CHECK - Task1 - NU modificați această linie!
;; ATENȚIE! NU modificați această secțiune!
(Task 1 : 50 puncte) ;;check-exp
(define A (create-tree '(8 9 10 15 8 5 0 1 4 5 9 7 1 0 151 651 61 45 416 2542 -8 3541 644 2 4 8542 51 142 215) functions)) ;;check-exp
(define B (create-tree '(942 4 54 64 94 25 0 -815 485 251 64 8 10 5 4 644 2 216 2541 5 8 7 5254 2542 214 4511) functions)) ;;check-exp
(define C (create-tree '(8 5 4 1 846 54 0 -5552 4 5 810 42 545 842 54 5488 8755 14 679 25 78 25 955 7891 789 8891 97 54 15 2465 155) functions)) ;;check-exp
(define D (create-tree '(8 9 1 5 9 7 5 9 78 1 5 6 9 89 24 52 95 22 94 6 485 18 6 97 8 100 4 9 655 478 92) functions)) ;;check-exp
(check-exp-part 'check-set1 .04 (test-task1 (create-tree '(8 4 2 1 -5 6 1 8 9 5 3 11 17 10 -6 4 8) functions) functions) result-check-set1)
(check-exp-part 'check-set2 .04 (test-task1 (create-tree '(-9 8 2 1 4 0 9 3 4 2 5 9 11 481 51 35 15 0 4 15 251 6551 12 3 4 7 9) functions) functions) result-check-set2)
(check-exp-part 'check-set3 .04 (test-task1 A functions) result-check-set3)
(check-exp-part 'check-set4 .04 (test-task1 B functions) result-check-set4)
(check-exp-part 'check-set5 .04 (test-task1 C functions) result-check-set5)
(check-exp-part 'union1 .005 (test-task1 (union A B) functions) result-union1)
(check-exp-part 'union2 .005 (test-task1 (union C D) functions) result-union2)
(check-exp-part 'union3 .005 (test-task1 (union A D) functions) result-union3)
(check-exp-part 'union4 .005 (test-task1 (union (union A B) (union C D)) functions) result-union4)
(check-exp-part 'intersection1 .01 (test-task1 (intersection A B) functions) result-intersection1)
(check-exp-part 'intersection2 .01 (test-task1 (intersection B C) functions) result-intersection2)
(check-exp-part 'intersection3 .01 (test-task1 (intersection C D) functions) result-intersection3)
(check-exp-part 'intersection4 .01 (test-task1 (intersection (intersection A B) (intersection  C D)) functions) result-intersection4)
(check-exp-part 'complements1 .01 (test-task1 (complements A B) functions) result-complements1)
(check-exp-part 'complements2 .01 (test-task1 (complements C D) functions) result-complements2)
(check-exp-part 'complements3 .01 (test-task1 (complements C D) functions) result-complements3)
(check-exp-part 'complements4 .01 (test-task1 (complements (complements A B) (complements C D)) functions) result-complements4)
(check-exp-part 'insert1 .005 (test-task1 (insert B -7) functions) result-insert1)
(check-exp-part 'insert2 .005 (test-task1 (insert A 59525) functions) result-insert2)
(check-exp-part 'insert3 .005 (test-task1 (insert C 988522) functions) result-insert3)
(check-exp-part 'insert4 .005 (test-task1 (insert D -812612) functions) result-insert4)
(check-exp-part 'remove1 .02 (test-task1 (remove binary-search-tree (minimum binary-search-tree)) functions) result-remove1)
(check-exp-part 'remove2 .02 (test-task1 (remove binary-search-tree 9) functions) result-remove2)
(check-exp-part 'remove3 .02 (test-task1 (remove binary-search-tree 3) functions) result-remove3)
(check-exp-part 'remove4 .02 (test-task1 (remove (remove (remove A (successor A 10)) (predecessor A 0)) 416) functions) result-remove4)
(check-exp-part 'complex1 .02 (test-task1 (union A (intersection B C)) functions) result-complex1)
(check-exp-part 'complex2 .02 (test-task1 (insert (intersection (complements B C) (remove (union A B) (predecessor A 51))) 7851) functions) result-complex2)
(check-exp-part 'complex3 .02 (test-task1 (insert (remove (remove (union (intersection (complements B C) (complements B A)) binary-search-tree) 214) 1) 1) functions) result-complex3)
(check-exp-part 'complex4 .02 (test-task1 (union (intersection (complements B A) (union C D)) (complements A D)) functions) result-complex4)
(check-exp-part 'complex5 .02 (test-task1 (intersection (union (complements A B) (complements C D)) (complements (intersection A B) (intersection C D))) functions) result-complex5)
(check-exp-part 'complex6 .02 (test-task1 (remove (insert (union (union (complements A B) (intersection C D)) (intersection (complements C D) (intersection A B))) 22) -8) functions) result-complex6)
(check-exp-part 'complex7 .02 (test-task1 (union (union (intersection A C) (complements A D)) (intersection (complements B C) (intersection B D))) functions) result-complex7)
(check-exp-part 'complex8 .02 (test-task1 (union (union (union A B) (union C D)) (intersection (intersection A B) (intersection C D))) functions) result-complex8)
(check-exp-part 'complex9 .02 (test-task1 (intersection (union (complements A B) (complements B A)) (intersection (union A B) (union C D))) functions) result-complex9)
(check-exp-part 'complex10 .02 (test-task1 (insert (remove (intersection (union (complements B A) (union (complements C D) (intersection A B))) (intersection (complements B (union A C)) (union C D))) 485) 100) functions) result-complex10)
(check-exp-part 'height-balanced1 .04 (check-self-balancing-tree B functions) #t)
(check-exp-part 'height-balanced2 .04 (check-self-balancing-tree A functions) #t)
(check-exp-part 'height-balanced3 .04 (check-self-balancing-tree C functions) #t)
(check-exp-part 'height-balanced4 .04 (check-self-balancing-tree D functions) #t)
(check-exp-part 'height-balanced5 .04 (let [(tree (create-tree '(1 2 3 4 5 6 7 8 9 10) functions))] (check-self-balancing-tree tree functions)) #t)
(check-exp-part 'height-balanced6 .04 (let [(tree (create-tree '(20 19 18 17 16 15 10 9 8 7 6 5 4 3 2 1) functions))] (check-self-balancing-tree tree functions)) #t)
(check-exp-part 'height-balanced7 .04 (let [(tree (union A (intersection B C)))] (check-self-balancing-tree tree functions)) #t)
(check-exp-part 'height-balanced8 .04 (let [(tree (remove (insert (union (complements A D) (intersection B C)) 24) 416))] (check-self-balancing-tree tree functions)) #t)
(check-exp-part 'height-balanced9 .04 (let [(tree (union (remove binary-search-tree 9) A))] (check-self-balancing-tree tree functions)) #t)
(check-exp-part 'height-balanced10 .04 (let [(tree (intersection (union (remove A (get-value A)) (remove B (get-value B))) (remove C (get-value C))))] (check-self-balancing-tree tree functions)) #t)
;; SFÂRȘIT CHECK - TASK 1 - NU modificați această linie!
;;
;; CHECK - TASK 2 - NU modificați această linie!
;; ATENȚIE! NU modificați această secțiune
(Task 2 : 20 puncte) ;;check-exp
(check-exp-part 'k-subsets1 0.1 (test-subsets (k-subsets (intersection A B) 8) result-k-subsets1) #t)
(check-exp-part 'k-subsets2 0.1 (let [(subsets (k-subsets binary-search-tree 11))] (and (= (length subsets) 78) (not (equal? (member '(2 3 5 6 8 9 11 12 13 15 21) subsets) #f)))) #t)
(check-exp-part 'k-subsets3 0.1 (test-subsets (k-subsets (create-tree '(1 2 3 4 5) functions) 3) result-k-subsets3) #t)
(check-exp-part 'k-subsets4 0.1 (test-subsets (k-subsets (create-tree '(8 7 6 5) functions) 2) result-k-subsets4) #t)
(check-exp-part 'k-subsets5 0.1 (test-subsets (k-subsets D 20) result-k-subsets5) #t)
(check-exp-part 'zig-zag-subsets1 0.1 (test-subsets (zig-zag-subsets (create-tree '(1 2 3 4 5 6) functions)) result-zig-zag1) #t)
(check-exp-part 'zig-zag-subsets2 0.1 (test-subsets (zig-zag-subsets (create-tree '(1 2 3 4) functions)) result-zig-zag2) #t)
(check-exp-part 'zig-zag-subsets3 0.1 (test-subsets (zig-zag-subsets (create-tree '(1 7 9 10 5) functions)) result-zig-zag3) #t)
(check-exp-part 'zig-zag-subsets4 0.1 (test-subsets (zig-zag-subsets (create-tree '(98 5 1 -85 -457) functions)) result-zig-zag4) #t)
(check-exp-part 'zig-zag-subsets5 0.1 (length (zig-zag-subsets (create-tree '(982 616 542 125 98 85) functions))) 122)
;; SFÂRȘIT CHECK - TASK 2 - NU modificați această linie!
;;
;; CHECK - BONUS - NU modificați această linie!
;; ATENȚIE! NU modificați această secțiune
(Bonus 3 : 20 puncte BONUS) ;;check-exp
(check-exp-part 'bonus1 0.1 (test-bonus (parser '(1 + (((2 * 3) - 4) * 5))) functions) 11)
(check-exp-part 'bonus2 0.1 (test-bonus (parser '((((5 + 8) * (9 - (8 / 2))) + (8 * 9)) * 10)) functions) 1370)
(check-exp-part 'bonus3 0.1 (test-bonus (parser '((5 * 8) - (7 * (3 + (5 * (10 / 2)))))) functions) -156)
(check-exp-part 'bonus4 0.1 (test-bonus (parser '(((((80 - 78) + 15) * 4 ) / 2) + (7 + (((5 * 3) - 2) * 4)))) functions) 93)
(check-exp-part 'bonus5 0.2 (test-bonus (parser '(((((((((5 + 8) + (9 + 8)) * 3) + (8 - 7)) * 2) + 10) / 2) * 10) - (5 + (7 + (8 * (1 + 2)))))) functions) 924)
(check-exp-part 'bonus6 0.2 (test-bonus (parser '((((((5 + 6) * 7) + 9) * 10) / 2) + (7 * (2 * (4 * (10 - (7 + (1 * (2 - 1))))))))) functions) 542)
(check-exp-part 'bonus7 0.2 (test-bonus (parser '(((5 + (7 - (2 * (3 + (9 - (7 + (8 + (5 * 2)))))))) + (5 * (((2 + 2) * (3 + 7)) + (7 * (9 - (4 + 7)))))) / 2)) functions) 84)
;; SFÂRȘIT CHECK - BONUS - NU modificați această linie!
;; SFÂRȘIT SECȚIUNE DE TESTARE

(sumar)
