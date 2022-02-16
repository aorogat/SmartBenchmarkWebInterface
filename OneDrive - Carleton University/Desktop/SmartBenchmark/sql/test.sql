 SELECT * --count(*) --"URI", "ContextSubject", "ContextObject", "ReducedPattern"
FROM public."NLP_Representation" 
WHERE NOT ("ReducedPattern" ~*  '.*null.*')
ORDER BY "URI", "ContextSubject", "ContextObject";

SELECT *
FROM public."Predicates" 
WHERE ("Label" ~*  '.*\s(above|across|against|along|among|around|at
	   |before|behind|below|beneath|beside|between|by|down|from|in
	   |into|near|on|to|toward|under|upon|with|within)$')
ORDER BY "URI", "Context_Subject", "Context_Object";


-- SELECT COUNT(*) FROM "Predicates" WHERE "processed" = 'YES';
-- SELECT * FROM "Predicates" WHERE "processed" = 'YES';



-- DELETE FROM public."NLP_Representation";

-- UPDATE public."Predicates" -SET "processed" = null;

-- select * from public."Predicates" ;