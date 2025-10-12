update SENTENCES as S1 set S1."END" = (
select S2."END" from
	ANNOTATIONS as A, SENTENCES as S2 where A.DOC_ID = S1.DOC_ID and A.DOC_ID = S2.DOC_ID
		and S1."BEGIN" <> S2."BEGIN"
		and A."BEGIN" < S1."END"    and A."END" > S1."END"
		and A."BEGIN" < S2."BEGIN"  and A."END" > S2."BEGIN"
) where exists (
select S2."END" from
	ANNOTATIONS as A, SENTENCES as S2 where A.DOC_ID = S1.DOC_ID and A.DOC_ID = S2.DOC_ID
		and S1."BEGIN" <> S2."BEGIN"
		and A."BEGIN" < S1."END"    and A."END" > S1."END"
		and A."BEGIN" < S2."BEGIN"  and A."END" > S2."BEGIN"
);

delete from SENTENCES SX where exists (
	select * from SENTENCES S where SX.DOC_ID = S.DOC_ID and SX."BEGIN" between S."BEGIN" and S."END" and SX."BEGIN" > S."BEGIN" and SX."END" = S."END"
)
;
