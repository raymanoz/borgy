import React, {Component} from "react";
import {RouteComponentProps} from "react-router";
import {server} from "../utils/server";
import {Trial} from "./Trial";
import "./Trial.css";

interface UrlParams {
    name: string;
}

interface AggregateTrialProps extends RouteComponentProps<UrlParams> {
}

interface AggregateTrialState {
    scales: string[];
}

export class AggregateTrial extends Component<AggregateTrialProps, AggregateTrialState> {
    constructor(props: Readonly<AggregateTrialProps>) {
        super(props);
        this.state = {
            scales: [],
        };
    }

    public componentDidMount() {
        fetch(server.trial(this.trialName()))
            .then((result) => result.json())
            .then((json) => {
                this.setState({scales: Object.entries(json.entries).map((e) => e[0])});
            });
    }

    public render = () => this.state.scales ?
        <div className={"container"}>
            <div className={"row"}>
                {this.state.scales.map((scale, idx) => <div key={idx} className={"col"}><Trial trialName={this.trialName()} scale={scale}/></div>)}
            </div>
        </div> : <span/>

    private trialName = () => this.props.match.params.name;
}
