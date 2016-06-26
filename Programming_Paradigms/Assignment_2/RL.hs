module RL where

import Data.Array
import System.Random
import Text.Printf
import Data.List.Split
import Data.Function
import Data.List

{-
    O stare este reprezentată ca un număr întreg.
    O cale este o listă de stări, eventual infinită.

    O estimare reprezintă o mulțime de asocieri (stare, informație aferentă).
    Cu toate că mediul este bidimensional, utilizăm o reprezentare liniară
    a acestuia, bazată pe un `Array`, cu stările indexate ca în figura din 
    enunț.
-}
type State      = Int
type Path       = [State]
type Estimation = Array State StateInfo

{-
    Lățimea și înălțimea mediului, precum și numărul de stări.
-}
width, height, nStates :: Int
width   = 4
height  = 3
nStates = width * height

{-
    Perechile de stări vecine.
-}
neighbors :: [(State, State)]
neighbors = [ (1, 2), (1, 5)
            , (2, 1), (2, 3)
            , (3, 2), (3, 4)
            , (3, 7)
            , (4, 3), (4, 8)
            , (5, 1), (5, 9)
            , (7, 3), (7, 8), (7, 11)
            , (8, 4), (8, 7), (8, 12)
            , (9, 5), (9, 10)
            , (10, 9), (10, 11)
            , (11, 7), (11, 10), (11, 12)
            , (12, 8), (12, 11)
            ]

{-
    Starea de pornire.
-}
startState :: State
startState = 1

{-
     Stările terminale.
-}
terminalStates :: [State]
terminalStates = [8, 12]

{-
    Rata de învățare alfa și factorul de scalare a acesteia.
-}
learningRate, scaleFactor :: Float
learningRate = 0.1
scaleFactor  = 0.999

-------------------------------------------------------------------------------
-- Completați sub această linie.


--  === 1. Generarea căilor ===

{-

    Întoarce toate stările vecine ale unei stări.
-}

neighborsOf :: State -> [State]
neighborsOf st = map (\p -> snd p) (filter (isNeighbor st) neighbors)

isNeighbor :: State->(State, State)-> Bool
isNeighbor st p = st == (fst p) 

{-

    Construiește o cale aleatoare infinită, pe baza unui generator.

    Hint: `Data.List.iterate`, `System.Random.split`.
-}
randomPath :: RandomGen g => g -> (Path, g)
randomPath g = ((genPath 1 1 (fst p)), (snd p)) 
 where p = System.Random.split g

genPath :: RandomGen g => State->State->g -> [State]
genPath prev st g 
  | ((elem st (neighborsOf prev)) == True) = prev : (genPath st (fst p) (snd p))
  | otherwise = (genPath prev (fst p) (snd p))
     where p = (randomR (0, 12) g)
{-
    *** TODO ***

    Trunchiază o cale, eventual infinită, la prima stare terminală.
-}
terminatePath :: Path -> Path
terminatePath l 
   | ((head l) == 8) || ((head l) == 12) = [head l]
   | otherwise = (head l) : (terminatePath (tail l))

{-
    *** TODO ***

    Construiește o infinitate de căi infinite.
-}
randomPaths :: RandomGen g => g -> [Path]
randomPaths g = (fst p) : (randomPaths (snd p))
   where p = (randomPath g)


--  === 2. Estimarea utilităților fără diminuarea ratei de învățare ===

{-
    *** TODO ***

    Array cu conscințele specifice fiecărei stări.
-}
reinforcements :: Array State Float
reinforcements = array (1,12) [(1::State, 0::Float), (2, 0), (3, 0), (4, 0),
                                (5, 0), (6,0),(7,0),(8,-1),(9,0),(10,0),(11,0),(12,1)]

{-
    *** TODO ***

    Valorile inițiale ale stărilor, înaintea rulării algoritmului.
    Se construiesc pe baza array-ului de consecințe.
-}
initialEstimation :: Estimation
initialEstimation = array (1,12) [(,) i (SI j 0) | (i,j)<-assocs reinforcements]

{-
    *** TODO ***

    Lista de utilități provenite dintr-o estimare.
-}
values :: Estimation -> [Float]
values e = [es | (SI es nr)<- (elems e)]

{-
    *** TODO ***

    Reprezentarea sub formă de șir de caractere a unei estimări.
    Se va întrebuința forma bidimensională, ca în imaginile din enunț.
    De asemenea, utilitățile vor fi rotunjite la 2 zecimale, și vor
    avea semnul inclus.

    Hint: `Text.Printf`.

    Exemplu de rezultat:

    -0.07 +0.06 +0.20 +1.00
    -0.20 +0.00 -0.43 -1.00
    -0.32 -0.45 -0.56 -0.78

    Pentru a vizualiza corect caracterele de linie nouă, aplicați
    în interpretor funcția `putStrLn` asupra șirului obținut.
-}
showEstimation :: Estimation -> String
showEstimation e = removeLastElem (foldl (\l y -> l ++ (estimationBuilder y))  "" x)
   where x = reverse (chunksOf width (map (\(SI est nr)->est)(elems e)))
 
removeLastElem :: String-> String
removeLastElem s = take (length s - 1) s

estimationBuilder :: [Float] -> String
estimationBuilder l = concatMap (printf "%+.2f ") (head (chunksOf (width - 1) l)) 
                 ++ printf "%+.2f" (last l) ++ printf "%c" '\n' 

{-
    *** TODO ***

    Actualizează o estimare în urmare parcurgerii unei căi.

    Hint: `Data.Array.accum`.
-}
updateEstimation :: Estimation -> Path -> Estimation
updateEstimation esti path = accum (\(SI es nr) y-> (SI (es + learningRate * (faster_exp nr) * 
         ((getReinforcement reinforcements (getNeighbor path y)) + (getEstimation esti y) - es)) (nr + 1))) esti l
   where l = zip (take ((length path) - 1) path) (drop 1 path)

getNeighbor :: Path -> State -> State
getNeighbor p elem = fst (head(filter (\(x,y) -> y == elem) l))
   where l = zip (take ((length p) - 1) p) (drop 1 p)

getEstimation :: Estimation -> State ->Float
getEstimation e s = snd (head (filter (\(x, y) -> x == s) intermediate))
   where intermediate = [(,) i j | (i, (SI j k))<-assocs e]

getReinforcement :: Array State Float -> State -> Float
getReinforcement r s = snd (head (filter (\(x,y)-> x == s) (assocs r)))

{-
    *** TODO ***

    Obține un flux infinit de estimări rafinate succesiv, pe baza unui flux
    infinit de căi finite, încheiate în stări terminale.

    Hint: `Data.List.mapAccumL`.
-}
estimations :: [Path] -> [Estimation]
estimations p =  [initialEstimation] ++ (snd (mapAccumL (\x y-> ( (updateEstimation x (terminatePath y)), 
                  (updateEstimation x (terminatePath y)) )) initialEstimation p ))

{-
    *** TODO ***

    Determină estimarea de rang dat ca parametru, pe baza unui generator.
-}
estimate :: RandomGen g => Int -> g -> Estimation
estimate n g = last $ take n (estimations (randomPaths g)) 

{-
    *** TODO ***

    Pentru o stare, determină vecinul cu cea mai mare valoare estimată.

    Hint: `Data.Function.on`.
-}

bestNeighborOf :: State -> Estimation -> State
bestNeighborOf s e = fst $ last $ sortBy (compare `on` snd) (filter (\(x,y) -> elem x neigh) whereToSearch)
   where    
       whereToSearch = [(x, es)| (x, (SI es nr)) <- (assocs e)]
       neigh = neighborsOf s

{-
    *** TODO ***

    Contruiește o cale începută în starea inițială, pe principiul alegerii 
    vecinului cu utilitata maximă.
-}
bestPath :: Estimation -> Path
bestPath e = terminatePath $ iterate (\x -> bestNeighborOf x e) 1


--  === 3. Estimarea utilităților cu diminuarea ratei de învățare ===

{-
    *** TODO ***

    Fluxul infinit al ratelor de învățare scalate:

    [ 1
    , learningRate
    , learningRate * scaleFactor
    , learningRate * scaleFactor^2
    , ...
    ]
-}
scaledLearningRates :: [Float]
scaledLearningRates = [1] ++ [learningRate * (faster_exp i) | i<-[1..]] 


expBySquaring :: (Int -> Float) -> Int -> Float
expBySquaring mf 0 = 1
expBySquaring mf 1 = scaleFactor
expBySquaring mf n 
   | (n `mod` 2 == 0) = mf (div n 2) * mf (div n 2)
   | n `mod` 2 == 1 = scaleFactor*mf (div (n-1) 2) * mf (div (n-1) 2)

exp_list :: [Float]
exp_list = map (expBySquaring faster_exp) [1..]

faster_exp :: Int -> Float
faster_exp n 
   | (n == 0) = 1
   | otherwise = exp_list !! (n-1) 

{-
    *** TODO ***

    Tip de date pentru reținerea atât a valorii estimate a unei stări,
    cât și a numărului de vizitări ale acesteia.
-}

data StateInfo = SI
  { estimated :: Float
  , numberOfVisits  :: Int
  } deriving Show
