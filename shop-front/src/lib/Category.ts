export type Category = {
  id: number;
  name: string;
  children?: Category[];
};

export const categories: Category[] = [
  {
    id: 1,
    name: "실내자율주행로봇",
    children: [
      { id: 11, name: "서비스안내형" },
      { id: 12, name: "서빙배송형" },
      { id: 13, name: "물류제조형" },
    ],
  },
  {
    id: 2,
    name: "실외자율주행로봇",
    children: [
      { id: 21, name: "순찰보안형" },
      { id: 22, name: "청소방역형" },
      { id: 23, name: "특수산업형" },
    ],
  },
];
